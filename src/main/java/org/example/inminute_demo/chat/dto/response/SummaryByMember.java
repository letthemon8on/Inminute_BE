package org.example.inminute_demo.chat.dto.response;

import lombok.Builder;

@Builder
public record SummaryByMember(
        String summary
) {
}
