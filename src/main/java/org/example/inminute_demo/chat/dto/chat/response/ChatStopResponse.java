package org.example.inminute_demo.chat.dto.chat.response;

import lombok.Builder;
import org.example.inminute_demo.chat.dto.flask.response.SummaryByMember;
import org.example.inminute_demo.chat.dto.gpt.response.CreateToDoResponse;
import org.example.inminute_demo.dto.toDo.response.ToDoResponse;

import java.util.List;

@Builder
public record ChatStopResponse(
        //
        String username,
        String nickname,
        Boolean isStart,
        //

        String summary,
        List<SummaryByMember> summaryByMemberList,
        List<ToDoResponse> toDoResponseList
) {
}
