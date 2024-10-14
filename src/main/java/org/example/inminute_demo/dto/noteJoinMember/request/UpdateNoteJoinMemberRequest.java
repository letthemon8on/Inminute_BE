package org.example.inminute_demo.dto.noteJoinMember.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteJoinMemberRequest {

    private String summary;
    private String todo;
}
