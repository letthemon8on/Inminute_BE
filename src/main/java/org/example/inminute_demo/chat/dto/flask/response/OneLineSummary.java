package org.example.inminute_demo.chat.dto.flask.response;

import lombok.Builder;

@Builder
public record OneLineSummary(
        String summary
) {
}
