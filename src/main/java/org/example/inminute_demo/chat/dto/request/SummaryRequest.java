package org.example.inminute_demo.chat.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record SummaryRequest(
        List<String> contents
) {
}
