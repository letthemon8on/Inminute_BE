package org.example.inminute_demo.zoom.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.example.inminute_demo.zoom.dto.request.ZoomMeetingDTO;
import org.example.inminute_demo.zoom.service.ZoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Zoom", description = "Zoom 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zoom")
public class ZoomController {

    private final ZoomService zoomService;

    @PostMapping("/create-meeting")
    public ApiResponse<?> createMeeting(@RequestBody ZoomMeetingDTO zoomMeetingDTO) throws IOException {
        zoomService.createMeeting(zoomMeetingDTO);
        return ApiResponse.onSuccess("Zoom 회의 생성 완료됨");
    }
}