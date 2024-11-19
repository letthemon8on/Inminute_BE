package org.example.inminute_demo.chat.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatStopResponse(
        String summary,
        List<SummaryByMember> summaryByMemberList,
        List<ToDoResponse> toDoResponseList
) {
}
