package org.example.inminute_demo.converter;

import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Schedule;
import org.example.inminute_demo.dto.schedule.request.CreateScheduleRequest;

import java.time.LocalDateTime;

public class ScheduleConverter {

    public static Schedule toSchedule(CreateScheduleRequest createScheduleRequest, LocalDateTime startDateTime, Member member) {

        return Schedule.builder()
                .name(createScheduleRequest.name())
                .color(createScheduleRequest.color())
                .startDateTime(startDateTime)
                .member(member)
                .build();
    }
}