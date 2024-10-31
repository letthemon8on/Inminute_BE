package org.example.inminute_demo.chat.dto.request;

public record ChatUpdateRequest(
        Long chatId,
        String nickname,
        String content
) {
}
