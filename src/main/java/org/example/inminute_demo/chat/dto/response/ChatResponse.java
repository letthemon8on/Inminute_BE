package org.example.inminute_demo.chat.dto.response;

import lombok.Builder;
import org.example.inminute_demo.chat.domain.MessageType;

import java.time.LocalDateTime;

@Builder
public record ChatResponse(
        Long id,
        Long memberId,
        String username,
        MessageType type,
        LocalDateTime createdAt,
        String content
) {
}
