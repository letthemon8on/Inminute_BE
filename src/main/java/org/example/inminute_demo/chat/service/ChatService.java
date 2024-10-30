package org.example.inminute_demo.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.converter.ChatConverter;
import org.example.inminute_demo.chat.domain.Chat;
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

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final TranscribeService transcribeService;
    private final NoteService noteService;

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

        String transcript = transcribeService.transcribeAudio(audioRequest.audioCode());

        Chat chat = ChatConverter.toChatFromTranscript(transcript, username, uuid);
        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat, header);
    }

    public ChatStatusResponse startChatting(String uuid) {

        noteService.updateIsStart(uuid, true);
        return ChatConverter.toChatStatusResponse(true);
    }

    public ChatStatusResponse stopChatting(String uuid) {

        noteService.updateIsStart(uuid, false);
        return ChatConverter.toChatStatusResponse(false);
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

    private String getValueFromHeader(Map<String, Object> header, String key) {
        return (String)header.get(key);
    }
}