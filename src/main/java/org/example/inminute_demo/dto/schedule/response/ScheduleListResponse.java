package org.example.inminute_demo.dto.schedule.response;

import java.util.List;

public record ScheduleListResponse(
        List<ScheduleResponse> schedules
) {
}