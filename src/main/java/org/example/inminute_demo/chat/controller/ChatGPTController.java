package org.example.inminute_demo.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.chat.dto.request.GPTTestRequest;
import org.example.inminute_demo.chat.dto.response.GPTTestListResponse;
import org.example.inminute_demo.chat.dto.response.GPTTestResponse;
import org.example.inminute_demo.chat.service.ChatGPTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    @PostMapping("/prompt")
    public ApiResponse<GPTTestListResponse> selectPrompt(@RequestBody GPTTestRequest gptTestRequest) {

        return ApiResponse.onSuccess(chatGPTService.prompt(gptTestRequest));
    }
}
