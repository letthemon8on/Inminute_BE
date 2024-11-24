package org.example.inminute_demo.chat.dto.flask.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SummaryResponse(
        OneLineSummary summary,
        List<SummaryByMember> summaryByMemberList
) {
}
