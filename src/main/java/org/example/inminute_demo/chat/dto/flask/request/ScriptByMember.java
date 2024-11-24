package org.example.inminute_demo.chat.dto.flask.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ScriptByMember(
        String username,
        String nickname,
        List<String> contents
) {
}
