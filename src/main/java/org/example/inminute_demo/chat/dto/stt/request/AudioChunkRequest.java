package org.example.inminute_demo.chat.dto.stt.request;

public record AudioChunkRequest(
        String nickname,
        String chunkCode
) {
}
