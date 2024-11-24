package org.example.inminute_demo.dto.toDo.response;

import java.util.List;

public record ToDoListResponse(
        List<ToDoResponse> toDoResponseList
) {
}
