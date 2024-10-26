package org.example.inminute_demo.chat.dto.request;

import org.example.inminute_demo.chat.domain.MessageType;

public record AudioRequest(
        MessageType type,
        String nickname,
        String audioCode // 바이트 코드로 변환하여 오디오 데이터 전송
) {
}
