package org.example.inminute_demo.chat.dto.response;

import org.springframework.data.domain.Page;

public record ChatsInNote(
        Page<ChatResponse> chats
) {
}
