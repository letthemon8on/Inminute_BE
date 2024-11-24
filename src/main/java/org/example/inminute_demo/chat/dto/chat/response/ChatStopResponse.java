package org.example.inminute_demo.chat.dto.chat.response;

import lombok.Builder;
import org.example.inminute_demo.chat.dto.flask.response.SummaryByMember;
import org.example.inminute_demo.chat.dto.gpt.response.ToDoResponse;

import java.util.List;

@Builder
public record ChatStopResponse(
        String summary,
        List<SummaryByMember> summaryByMemberList,
        List<ToDoResponse> toDoResponseList
) {
}
