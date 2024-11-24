package org.example.inminute_demo.dto.schedule.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScheduleResponse(
        Long id,
        String name,
        String color,
        LocalDateTime startDateTime
) {
}