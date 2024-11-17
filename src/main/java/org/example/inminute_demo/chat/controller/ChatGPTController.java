package org.example.inminute_demo.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.chat.dto.request.CompletionRequest;
import org.example.inminute_demo.chat.dto.response.CreateToDoResponse;
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
    public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody CompletionRequest completionRequest) {
        log.debug("param :: " + completionRequest.toString());
        Map<String, Object> result = chatGPTService.prompt(completionRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
