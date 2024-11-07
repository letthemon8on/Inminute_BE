package org.example.inminute_demo.dto.schedule.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CreateScheduleRequest(
        String name,
        String color,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime startDateTime
) {
}