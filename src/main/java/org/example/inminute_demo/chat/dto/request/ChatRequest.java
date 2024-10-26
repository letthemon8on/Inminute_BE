package org.example.inminute_demo.chat.dto.request;

import org.example.inminute_demo.chat.domain.MessageType;

public record ChatRequest(
        MessageType type,
        String nickname,
        String content,

        String audioCode // 바이트 코드로 오디오 데이터 수신
) {
}
