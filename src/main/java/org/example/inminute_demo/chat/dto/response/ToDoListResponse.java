package org.example.inminute_demo.chat.dto.response;

import java.util.List;

public record ToDoListResponse(
        List<ToDoResponse> toDoResponses
) {
}
