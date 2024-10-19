package org.example.inminute_demo.chat.dto.request;

import org.example.inminute_demo.chat.domain.MessageType;

public record ChatRequest(
        MessageType type,
        String username,
        String content
) {
}
