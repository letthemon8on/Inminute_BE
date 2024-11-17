package org.example.inminute_demo.chat.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record CompletionRequest(
        String model,
        List<ChatCompletionRequest> messages
) {

    @Builder
    public record ChatCompletionRequest(
            String role,
            String content
    ){
    }
}
