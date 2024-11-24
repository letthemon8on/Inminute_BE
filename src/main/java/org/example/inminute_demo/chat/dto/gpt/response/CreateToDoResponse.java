package org.example.inminute_demo.chat.dto.gpt.response;

import java.util.List;

public record CreateToDoResponse(
        String username,
        List<ToDoList> toDoLists
) {
}
