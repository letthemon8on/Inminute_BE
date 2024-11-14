package org.example.inminute_demo.dto.noteJoinMember.response;

import java.util.List;

public record NoteJoinMemberListResponse(
        List<NoteJoinMemberResponse> noteJoinMemberResponses
) {
}
