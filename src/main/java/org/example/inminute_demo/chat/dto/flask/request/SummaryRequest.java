package org.example.inminute_demo.chat.dto.flask.request;

import lombok.Builder;

import java.util.List;

@Builder
public record SummaryRequest(
        MeetingScript script,
        List<ScriptByMember> scriptByMemberList
) {
}
