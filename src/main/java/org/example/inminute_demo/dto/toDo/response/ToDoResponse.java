package org.example.inminute_demo.dto.toDo.response;

import lombok.Builder;

@Builder
public record ToDoResponse(
        Long id,
        String uuid,
        String username,
        String nickname,
        String content,
        Boolean isDone
) {
}
