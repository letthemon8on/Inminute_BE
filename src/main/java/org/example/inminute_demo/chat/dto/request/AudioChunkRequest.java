package org.example.inminute_demo.chat.dto.request;

public record AudioChunkRequest(
        String nickname,
        byte[] audioChunk,
        Boolean isLast
) {
}
