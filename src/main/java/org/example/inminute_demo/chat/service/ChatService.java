package org.example.inminute_demo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.chat.converter.ChatConverter;
import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.domain.MessageType;
import org.example.inminute_demo.chat.dto.request.*;
import org.example.inminute_demo.chat.dto.response.*;
import org.example.inminute_demo.chat.exception.WebSocketException;
import org.example.inminute_demo.chat.repository.ChatRepository;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.domain.NoteJoinMember;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.repository.NoteRepository;
import org.example.inminute_demo.service.MemberService;
import org.example.inminute_demo.service.NoteJoinMemberService;
import org.example.inminute_demo.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static jakarta.xml.bind.DatatypeConverter.parseBase64Binary;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final TranscribeService transcribeService;
    private final NoteService noteService;
    private final NoteJoinMemberService noteJoinMemberService;
    private final SummaryService summaryService;
    private final MemberRepository memberRepository;

    // 사용자 발언 청크 저장용 Map
    private final Map<String, ByteArrayOutputStream> chunkBufferMap = new ConcurrentHashMap<>();

    // 1분 동안의 음성 데이터 바이트 제한 (5.76MB)
    private static final int MAX_AUDIO_LENGTH = 5760000;

    // 채팅 내역 저장
    @Transactional
    public ChatResponse save(ChatRequest chatRequest, String uuid, Map<String, Object> header) {

        String username = getValueFromHeader(header, "username");
        Chat chat = ChatConverter.toChat(chatRequest, username, uuid);
        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat, header);
    }

    @Transactional
    public ChatResponse transcribe(AudioRequest audioRequest, String uuid, Map<String, Object> header) {

        String username = getValueFromHeader(header, "username");

        log.debug("audioCode: " + audioRequest.audioCode());

        String transcript = transcribeService.transcribeAudio(audioRequest.audioCode());
        Chat chat = ChatConverter.toChatFromTranscript(transcript, username, uuid);
        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat, header);
    }

    @Transactional
    public ChatResponse transcribeByChunk(AudioChunkRequest audioChunkRequest, String uuid, Map<String, Object> header) {

        String username = getValueFromHeader(header, "username");

        try {
            if (audioChunkRequest.chunkCode().equals("END")) {
                // 마지막 청크일 경우 버퍼에서 데이터 병합 후 처리
                ByteArrayOutputStream buffer = chunkBufferMap.get(username);
                if (buffer == null) {
                    throw new WebSocketException("No audio data available for user");
                }

                byte[] completeAudio = buffer.toByteArray();

                // 길이 검사 추가 (예: 제한 1분으로 가정)
                if (completeAudio.length > MAX_AUDIO_LENGTH) {
                    chunkBufferMap.remove(username);
                }

                // 병합된 오디오 데이터로 변환 및 저장
                String transcript = transcribeService.transcribeAudioChunk(completeAudio);

                // 채팅 메시지 생성 및 저장
                Chat chat = ChatConverter.toChatFromTranscript(transcript, username, uuid);
                Chat savedChat = chatRepository.save(chat);

                // 버퍼 초기화
                chunkBufferMap.remove(username);
                buffer.close();

                return toChatResponse(savedChat, header);
            }

            // 청크 데이터를 Base64 디코딩 후 버퍼에 저장
            byte[] byteChunk = parseBase64Binary(audioChunkRequest.chunkCode());
            ByteArrayOutputStream buffer = chunkBufferMap.computeIfAbsent(username, k -> new ByteArrayOutputStream());
            buffer.write(byteChunk);

        } catch (IOException e) {
            log.error("Error processing audio chunk for user {}: {}", username, e.getMessage());
            throw new WebSocketException("Failed to process audio chunk");
        }

        Chat tempChat = Chat.builder()
                .username(username)
                .type(MessageType.CONVERTING)
                .content("변환중")
                .uuid(uuid)
                .build();

        return toChatResponse(tempChat, header); // 마지막 청크가 아닐 경우
    }

    @Transactional
    public ChatResponse update(ChatUpdateRequest chatUpdateRequest,  Map<String, Object> header) {

        Chat chat = chatRepository.findById(chatUpdateRequest.chatId())
                .orElseThrow(() -> new WebSocketException("존재하지 않는 채팅입니다."));

        chat.updateContent(chatUpdateRequest.content());
        chat.updateType(MessageType.EDIT);
        chatRepository.save(chat);

        return toChatResponse(chat, header);
    }

    public ChatStatusResponse startChatting(String uuid, Map<String, Object> header) {

        noteService.updateIsStart(uuid, true);
        return toChatStatusResponse(true, header);
    }

    public ChatStatusResponse stopChattingStatus(String uuid, Map<String, Object> header) {

        noteService.updateIsStart(uuid, false);
        return toChatStatusResponse(false, header);
    }

    @Transactional
    public ChatStopResponse stopChatting(String uuid) throws JsonProcessingException {

        List<ChatResponse> script = chatRepository.findAllByNoteUUID(uuid);

        SummaryRequest summaryRequest = new SummaryRequest(script.stream()
                .map(ChatResponse::content)
                .collect(Collectors.toList()));

        String oneLineSummary = summaryService.getSummaryFromFlask(summaryRequest);

        noteService.updateSummary(uuid, oneLineSummary);

        // username 기준으로 그룹화
        Map<String, List<ChatResponse>> scriptByUsername = script.stream()
                .collect(Collectors.groupingBy(ChatResponse::username));

        List<SummaryByMember> summaryByMemberList = new ArrayList<>();
        for (Map.Entry<String, List<ChatResponse>> entry : scriptByUsername.entrySet()) {
            // 각 username에 해당하는 ChatResponse 리스트를 가져옵니다.
            List<ChatResponse> chatListByUsername = entry.getValue();

            // ChatResponse 리스트의 content만 추출하여 SummaryRequest 생성
            SummaryRequest summaryRequestByUsername = new SummaryRequest(
                    chatListByUsername.stream()
                            .map(ChatResponse::content)  // content 추출
                            .collect(Collectors.toList())
            );

            // Flask에서 요약 생성
            String summaryByUsername = summaryService.getSummaryFromFlask(summaryRequestByUsername);

            // 생성된 요약을 저장
            noteJoinMemberService.updateSummary(uuid, entry.getKey(), summaryByUsername);

            Member member = memberRepository.findByUsername(entry.getKey())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

            SummaryByMember summaryByMember = SummaryByMember.builder()
                    .username(entry.getKey())
                    .nickname(member.getNickname())
                    .summary(summaryByUsername).build();
            summaryByMemberList.add(summaryByMember);
        }
        return ChatConverter.toChatStopResponse(oneLineSummary, summaryByMemberList);
    }

    // 채팅 내역 조회(페이징)
    public ChatsInNote getByNoteUUID(String uuid, Pageable pageable) {
        Page<ChatResponse> result = chatRepository.findByNoteUUID(uuid, pageable);
        return new ChatsInNote(result);
    }

    // 모든 채팅 내역 조회
    public List<ChatResponse> getAllByNoteUUID(String uuid) {
        return chatRepository.findAllByNoteUUID(uuid);
    }

    private ChatResponse toChatResponse(Chat chat, Map<String, Object> header) {
        String username = getValueFromHeader(header, "username");
        String nickname = getValueFromHeader(header, "nickname");

        return ChatConverter.toChatResponse(chat, username, nickname);
    }

    private ChatStatusResponse toChatStatusResponse(Boolean isStart, Map<String, Object> header) {
        String username = getValueFromHeader(header, "username");
        String nickname = getValueFromHeader(header, "nickname");

        return ChatConverter.toChatStatusResponse(isStart, username, nickname);
    }

    private String getValueFromHeader(Map<String, Object> header, String key) {
        return (String)header.get(key);
    }

    private byte[] mergeChunks(List<byte[]> chunks) {
        int totalLength = chunks.stream().mapToInt(chunk -> chunk.length).sum();
        byte[] mergedAudio = new byte[totalLength];
        int currentPos = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, mergedAudio, currentPos, chunk.length);
            currentPos += chunk.length;
        }
        return mergedAudio;
    }
}