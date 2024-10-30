package org.example.inminute_demo.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.chat.converter.ChatConverter;
import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.dto.request.AudioChunkRequest;
import org.example.inminute_demo.chat.dto.request.AudioRequest;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.dto.response.ChatResponse;
import org.example.inminute_demo.chat.dto.response.ChatStatusResponse;
import org.example.inminute_demo.chat.dto.response.ChatsInNote;
import org.example.inminute_demo.chat.repository.ChatRepository;
import org.example.inminute_demo.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final TranscribeService transcribeService;
    private final NoteService noteService;

    // 사용자 발언 청크 저장용 Map
    private final Map<String, List<byte[]>> chunkMap = new ConcurrentHashMap<>();

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

        // audioRequest의 청크 데이터를 그대로 추가
        chunkMap.computeIfAbsent(username, k -> new ArrayList<>()).add(audioChunkRequest.audioChunk());

        // 마지막 청크인지 확인
        if (audioChunkRequest.isLast()) {
            // 모든 청크를 병합한 후 변환 진행
            byte[] completeAudio = mergeChunks(chunkMap.get(username));
            String transcript = transcribeService.transcribeAudioChunk(completeAudio);

            // 병합된 오디오 데이터로 채팅 메시지 생성
            Chat chat = ChatConverter.toChatFromTranscript(transcript, username, uuid);
            Chat savedChat = chatRepository.save(chat);

            // 청크 리스트 초기화
            chunkMap.remove(username);

            return toChatResponse(savedChat, header);
        }

        return toChatResponse(null, header); // 마지막 청크가 아닐 경우
    }

    public ChatStatusResponse startChatting(String uuid, Map<String, Object> header) {

        noteService.updateIsStart(uuid, true);
        return toChatStatusResponse(true, header);
    }

    public ChatStatusResponse stopChatting(String uuid, Map<String, Object> header) {

        noteService.updateIsStart(uuid, false);
        return toChatStatusResponse(false, header);
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