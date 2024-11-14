package org.example.inminute_demo.dto.toDo.request;

public record UpdateToDoRequest(
        String content,
        Boolean isDone
) {
}
