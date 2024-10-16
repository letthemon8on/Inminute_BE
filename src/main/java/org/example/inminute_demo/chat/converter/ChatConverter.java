package org.example.inminute_demo.chat.converter;

import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.dto.response.ChatResponse;

public class ChatConverter {

    public static Chat toChat(ChatRequest chatRequest, Long noteId) {
        return Chat.builder()
                .memberId(chatRequest.memberId())
                .type(chatRequest.type())
                .content(chatRequest.content())
                .noteId(noteId)
                .build();
    }

    public static ChatResponse toChatResponse(Chat chat, String username) {
        return ChatResponse.builder()
                .memberId(chat.getMemberId())
                .username(username)
                .type(chat.getType())
                .createdAt(chat.getCreated_at())
                .content(chat.getContent())
                .build();
    }
}
