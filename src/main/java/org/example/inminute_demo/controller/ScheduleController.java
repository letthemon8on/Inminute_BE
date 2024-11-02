package org.example.inminute_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.dto.schedule.request.CreateScheduleRequest;
import org.example.inminute_demo.dto.schedule.response.ScheduleListResponse;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.example.inminute_demo.service.ScheduleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule", description = "일정 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "일정 생성", description = "yyyy-MM-dd HH:mm 형식으로 시작 시간을 지정하고 일정을 생성합니다.")
    public ApiResponse<?> createSchedule(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                         @RequestBody CreateScheduleRequest createScheduleRequest) {

        scheduleService.createSchedule(customOAuth2User, createScheduleRequest);
        return ApiResponse.onSuccess("일정 생성 성공");
    }

    @GetMapping
    @Operation(summary = "월별 일정 조회", description = "쿼리 파라미터로 년, 월을 지정하여 월별로 일정 리스트를 조회합니다.")
    public ApiResponse<ScheduleListResponse> getScheduleListByMonth(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                                    @RequestParam(name = "year") Integer year,
                                                                    @RequestParam(name = "month") Integer month) {

        return ApiResponse.onSuccess(scheduleService.getAllScheduleByMonth(customOAuth2User, year, month));
    }
}