package org.example.inminute_demo.zoom.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.example.inminute_demo.zoom.service.SpeechToTextService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Google STT", description = "Google STT 관련 API 입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stt")
public class SttController {

    private final SpeechToTextService speechToTextService;

    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> handleAudioMessage(@RequestParam("audioFile") MultipartFile audioFile, @RequestParam Long noteId) throws IOException {
        speechToTextService.transcribe(audioFile, noteId);
        return ApiResponse.onSuccess("텍스트 변환 완료됨");
    }
}