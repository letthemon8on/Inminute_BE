package org.example.inminute_demo.dto.schedule.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record CreateScheduleRequest(
        String name,
        String color,

        List<String> dateList,
        String startTime
) {
}