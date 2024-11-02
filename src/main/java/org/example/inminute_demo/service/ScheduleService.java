package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.Handler.TempHandler;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.converter.ScheduleConverter;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Schedule;
import org.example.inminute_demo.dto.schedule.request.CreateScheduleRequest;
import org.example.inminute_demo.dto.schedule.request.UpdateScheduleRequest;
import org.example.inminute_demo.dto.schedule.response.ScheduleListResponse;
import org.example.inminute_demo.dto.schedule.response.ScheduleResponse;
import org.example.inminute_demo.repository.schedule.ScheduleCustomRepository;
import org.example.inminute_demo.repository.schedule.ScheduleRepository;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleCustomRepository scheduleCustomRepository;
    private final MemberService memberService;

    @Transactional
    public void createSchedule(CustomOAuth2User customOAuth2User, CreateScheduleRequest createScheduleRequest) {

        Member member = memberService.loadMemberByCustomOAuth2User(customOAuth2User);

        Schedule schedule = ScheduleConverter.toSchedule(createScheduleRequest, member);
        scheduleRepository.save(schedule);
    }

    public ScheduleListResponse getAllScheduleByMonth(CustomOAuth2User customOAuth2User, Integer year, Integer month) {

        Member member = memberService.loadMemberByCustomOAuth2User(customOAuth2User);

        List<Schedule> schedules = scheduleCustomRepository.findByYearAndMonth(year, month, member);

        List<ScheduleResponse> scheduleResponses = schedules.stream()
                .map(schedule -> ScheduleResponse.builder()
                        .name(schedule.getName())
                        .startDateTime(schedule.getStartDateTime())
                        .build())
                .collect(Collectors.toList());

        return new ScheduleListResponse(scheduleResponses);
    }

    @Transactional
    public void updateSchedule(Long scheduleId, UpdateScheduleRequest updateScheduleRequest) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

        schedule.updateName(updateScheduleRequest.name());
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

        scheduleRepository.delete(schedule);
    }
}