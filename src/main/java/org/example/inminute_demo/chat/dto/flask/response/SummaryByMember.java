package org.example.inminute_demo.chat.dto.flask.response;

import lombok.Builder;

@Builder
public record SummaryByMember(
        String username,
        String nickname,
        String summary
) {
}
