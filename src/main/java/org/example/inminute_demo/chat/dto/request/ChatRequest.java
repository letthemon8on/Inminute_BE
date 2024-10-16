package org.example.inminute_demo.chat.dto.request;

import org.example.inminute_demo.chat.domain.MessageType;

public record ChatRequest(
        MessageType type,
        Long memberId,
        String content
) {
}
