package org.example.inminute_demo.converter;

import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Schedule;
import org.example.inminute_demo.dto.schedule.request.CreateScheduleRequest;

public class ScheduleConverter {

    public static Schedule toSchedule(CreateScheduleRequest createScheduleRequest, Member member) {

        return Schedule.builder()
                .name(createScheduleRequest.name())
                .color(createScheduleRequest.color())
                .startDateTime(createScheduleRequest.startDateTime())
                .member(member)
                .build();
    }
}