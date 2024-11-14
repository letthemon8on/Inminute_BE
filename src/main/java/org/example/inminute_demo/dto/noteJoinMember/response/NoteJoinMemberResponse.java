package org.example.inminute_demo.dto.noteJoinMember.response;

import lombok.Builder;
import org.example.inminute_demo.domain.ToDo;

import java.util.List;

@Builder
public record NoteJoinMemberResponse(
        Long id,
        String nickname,
        String summary,
        List<ToDoResponse> toDoList
) {
    @Builder
    public record ToDoResponse(
            String content,
            Boolean isDone
    ) {
    }
}
