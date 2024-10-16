package org.example.inminute_demo.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.converter.ChatConverter;
import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.dto.response.ChatResponse;
import org.example.inminute_demo.chat.dto.response.ChatsInNote;
import org.example.inminute_demo.chat.repository.ChatRepository;
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

    // 채팅 내역 저장
    @Transactional
    public ChatResponse save(ChatRequest chatRequest, Long noteId, Map<String, Object> header) {
        Chat chat = ChatConverter.toChat(chatRequest, noteId);
        Chat savedChat = chatRepository.save(chat);

        return toChatResponse(savedChat, header);
    }

    // 채팅 내역 조회(페이징)
    public ChatsInNote getByNoteId(Long noteId, Pageable pageable) {
        Page<ChatResponse> result = chatRepository.findByNoteId(noteId, pageable);
        return new ChatsInNote(result);
    }

    // 모든 채팅 내역 조회
    public List<ChatResponse> getAllByNoteId(Long noteId) {
        return chatRepository.findAllByNoteId(noteId);
    }

    private ChatResponse toChatResponse(Chat chat, Map<String, Object> header) {
        String username = getValueFromHeader(header, "username");
        String profileImgUrl = getValueFromHeader(header, "profileImgUrl");

        return ChatConverter.toChatResponse(chat, username);
    }

    private String getValueFromHeader(Map<String, Object> header, String key) {
        return (String)header.get(key);
    }
}