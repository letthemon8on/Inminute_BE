package org.example.inminute_demo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.chat.converter.ChatConverter;
import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.domain.MessageType;
import org.example.inminute_demo.chat.dto.chat.request.ChatRequest;
import org.example.inminute_demo.chat.dto.chat.request.ChatUpdateRequest;
import org.example.inminute_demo.chat.dto.chat.response.ChatResponse;
import org.example.inminute_demo.chat.dto.chat.response.ChatStatusResponse;
import org.example.inminute_demo.chat.dto.chat.response.ChatStopResponse;
import org.example.inminute_demo.chat.dto.chat.response.ChatsInNote;
import org.example.inminute_demo.chat.dto.flask.request.MeetingScript;
import org.example.inminute_demo.chat.dto.flask.request.ScriptByMember;
import org.example.inminute_demo.chat.dto.flask.request.SummaryRequest;
import org.example.inminute_demo.chat.dto.flask.response.SummaryByMember;
import org.example.inminute_demo.chat.dto.flask.response.SummaryResponse;
import org.example.inminute_demo.chat.dto.gpt.request.QuestionRequest;
import org.example.inminute_demo.chat.dto.gpt.response.AnswerResponse;
import org.example.inminute_demo.chat.dto.gpt.response.CreateToDoResponse;
import org.example.inminute_demo.chat.dto.stt.request.AudioChunkRequest;
import org.example.inminute_demo.chat.dto.stt.request.AudioRequest;
import org.example.inminute_demo.chat.exception.WebSocketException;
import org.example.inminute_demo.chat.repository.ChatRepository;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.dto.toDo.response.ToDoResponse;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.service.NoteJoinMemberService;
import org.example.inminute_demo.service.NoteService;
import org.example.inminute_demo.service.ToDoService;
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
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final TranscribeService transcribeService;
    private final NoteService noteService;
    private final NoteJoinMemberService noteJoinMemberService;
    private final SummaryService summaryService;
    private final ChatGPTService chatGPTService;
    private final ToDoService toDoService;
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
    public ChatResponse update(ChatUpdateRequest chatUpdateRequest, Map<String, Object> header) {

        Chat chat = chatRepository.findById(chatUpdateRequest.chatId())
                .orElseThrow(() -> new WebSocketException("존재하지 않는 채팅입니다."));

        chat.updateContent(chatUpdateRequest.content());
        chat.updateType(MessageType.EDIT);
        chatRepository.save(chat);

        return toChatResponse(chat, header);
    }

    public ChatStatusResponse startChatting(String uuid, Map<String, Object> header) {
        return toChatStatusResponse(true, header);
    }

    public ChatStatusResponse stopChattingStatus(String uuid, Map<String, Object> header) {
        return toChatStatusResponse(false, header);
    }

    @Transactional
    public ChatStopResponse stopChatting(String uuid, Map<String, Object> header) throws JsonProcessingException {

        System.out.println("----------stopChatting 호출됨----------");

        List<ChatResponse> script = chatRepository.findAllByNoteUUID(uuid);

        MeetingScript meetingScript = new MeetingScript(script.stream()
                .map(ChatResponse::content)
                .collect(Collectors.toList()));

        // username 기준으로 그룹화
        Map<String, List<ChatResponse>> scriptByUsername = script.stream()
                .collect(Collectors.groupingBy(ChatResponse::username));

        List<ScriptByMember> scriptByMemberList = new ArrayList<>();
        for (Map.Entry<String, List<ChatResponse>> entry : scriptByUsername.entrySet()) {
            // 각 username에 해당하는 ChatResponse 리스트를 가져옵니다.
            List<ChatResponse> chatListByUsername = entry.getValue();

            Member member = memberRepository.findByUsername(entry.getKey())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

            // ChatResponse 리스트의 content만 추출하여 SummaryRequest 생성
            ScriptByMember scriptByMember = ScriptByMember.builder()
                    .username(entry.getKey())
                    .nickname(member.getNickname())
                    .contents(chatListByUsername.stream()
                            .map(ChatResponse::content)  // content 추출
                            .collect(Collectors.toList()))
                    .build();

            scriptByMemberList.add(scriptByMember);
        }

        SummaryRequest summaryRequest = SummaryRequest.builder()
                .script(meetingScript)
                .scriptByMemberList(scriptByMemberList)
                .build();

        SummaryResponse summaryResponse = summaryService.getSummaryFromFlask(summaryRequest);

        noteService.updateSummary(uuid, summaryResponse.oneLineSummary().summary());

        for (SummaryByMember summaryByMember : summaryResponse.summaryByMemberList()) {
            noteJoinMemberService.updateSummary(uuid, summaryByMember.username(), summaryByMember.summary());
        }

        List<ToDoResponse> toDoResponseList = getToDo(uuid);

        //
        String username = getValueFromHeader(header, "username");
        String nickname = getValueFromHeader(header, "nickname");
        //
        return ChatConverter.toChatStopResponse(username, nickname,
                summaryResponse.oneLineSummary(), summaryResponse.summaryByMemberList(),
                toDoResponseList);
    }

    @Transactional
    public List<ToDoResponse> getToDo(String uuid) {

        System.out.println("----------getToDo 호출됨----------");

        List<ChatResponse> chatResponses = chatRepository.findAllByNoteUUID(uuid);

        String script = chatResponses.stream()
                .map(chatResponse -> chatResponse.nickname() + ": " + chatResponse.content())
                .collect(Collectors.joining(" "));

        String usernameList = chatResponses.stream()
                .map(ChatResponse::username)
                .distinct()
                .collect(Collectors.joining(", "));

        String prompt = script + "\n\n여기까지가 회의록이야.\n" + usernameList + "의 todo 리스트를 만들어줘. 각 할 일은 13자 이내로 만들어줘." +
                "\n다음의 예시를 참고해서 만들어줘. 반드시 예시의 형식을 지켜야해.\n" +
                "googleabc123@google.com:\n1. 와이어프레임 만들기\n2. 기획안 수정하기\n3. 프로젝트 일정 조정하기\n\n" +
                "kakaoqwe456@naver.com:\n1. ERD 작성하기\n2. API 명세서 완성하기\n\n";
        System.out.println(prompt);

        List<CreateToDoResponse> createToDoResponses = chatGPTService.todo(prompt);
        toDoService.saveToDo(createToDoResponses, uuid);

        return toDoService.findAll(uuid);
    }

    public AnswerResponse getAnswer(String uuid, QuestionRequest questionRequest) {
        List<ChatResponse> chatResponses = chatRepository.findAllByNoteUUID(uuid);

        String script = chatResponses.stream()
                .map(chatResponse -> chatResponse.nickname() + ": " + chatResponse.content())
                .collect(Collectors.joining(" "));

        String prompt = "너는 우리 서비스의 챗봇 역할을 담당해야 해. 회의 스크립트를 보내줄게.\n\n" +
                script + "\n\n위 대화는 회의 스크립트야.\n" +
                "이 대화내용을 바탕으로 질문을 할건데, 답변은 존댓말로 해줘.\n" +
                questionRequest.question();
        System.out.println(prompt);

        return chatGPTService.question(prompt);
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