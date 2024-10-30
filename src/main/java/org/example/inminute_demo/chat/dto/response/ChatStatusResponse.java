package org.example.inminute_demo.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatStatusResponse(
        String username,
        String nickname,
        Boolean isStart
) {
}
