package org.example.inminute_demo.zoom.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.example.inminute_demo.zoom.dto.request.ZoomMeetingDTO;
import org.example.inminute_demo.zoom.service.ZoomService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Zoom", description = "Zoom 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost/5173")
@RequestMapping("/api/zoom")
public class ZoomController {

    private final ZoomService zoomService;

    @PostMapping("/create-meeting")
    public ApiResponse<?> createMeeting(@RequestParam("noteId") Long noteId) throws IOException {
        zoomService.createMeeting(noteId);
        return ApiResponse.onSuccess("Zoom 회의 생성 완료됨");
    }
}