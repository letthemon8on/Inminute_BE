package org.example.inminute_demo.chat.converter;

import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.dto.response.ChatResponse;

public class ChatConverter {

    public static Chat toChat(ChatRequest chatRequest, String uuid) {
        return Chat.builder()
                .username(chatRequest.username())
                .type(chatRequest.type())
                .content(chatRequest.content())
                .uuid(uuid)
                .build();
    }

    public static ChatResponse toChatResponse(Chat chat, String username, String nickname) {
        return ChatResponse.builder()
                .username(username)
                .nickname(nickname)
                .type(chat.getType())
                .createdAt(chat.getCreated_at())
                .content(chat.getContent())
                .build();
    }
}
