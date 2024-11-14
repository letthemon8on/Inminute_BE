package org.example.inminute_demo.dto.noteJoinMember.request;

public record UpdateNoteJoinMemberRequest(
        Long noteJoinMemberId,
        String summary
) {
}
