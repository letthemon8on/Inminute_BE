package org.example.inminute_demo.chat.dto.gpt.response;

import java.util.List;

public record ToDoResponse(
        String nickname,
        List<ToDoList> toDoLists
) {
}
