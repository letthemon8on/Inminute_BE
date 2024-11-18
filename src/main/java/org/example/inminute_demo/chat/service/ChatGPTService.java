package org.example.inminute_demo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.chat.config.ChatGPTConfig;
import org.example.inminute_demo.chat.dto.request.GPTRequest;
import org.example.inminute_demo.chat.dto.request.GPTTestRequest;
import org.example.inminute_demo.chat.dto.response.GPTResponse;
import org.example.inminute_demo.chat.dto.response.GPTTestListResponse;
import org.example.inminute_demo.chat.dto.response.GPTTestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.prompt-url}")
    private String promptUrl;

    public GPTTestListResponse prompt(GPTTestRequest gptTestRequest) {

        HttpHeaders headers = chatGPTConfig.httpHeaders();

        GPTRequest gptRequest = new GPTRequest(model, gptTestRequest.question());

        HttpEntity<GPTRequest> request = new HttpEntity<>(gptRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        GPTResponse gptResponse = restTemplate.postForObject(promptUrl, request, GPTResponse.class);

        if (gptResponse == null || gptResponse.getChoices() == null || gptResponse.getChoices().isEmpty()) {
            throw new RuntimeException();
        }

        System.out.println(gptResponse.getChoices().get(0).getMessage().getContent());

        List<GPTTestResponse> gptTestResponses = extractTodos(gptResponse.getChoices().get(0).getMessage().getContent());

        return new GPTTestListResponse(gptTestResponses);
    }

    public List<GPTTestResponse> extractTodos(String input) {

        // 각 사용자와 할 일 목록을 매칭하기 위한 정규식
        Pattern pattern = Pattern.compile("(username\\d+):\\n((?:\\d+\\. .+\\n?)+)");
        Matcher matcher = pattern.matcher(input);

        List<GPTTestResponse> gptTestResponses = new ArrayList<>();
        while (matcher.find()) {
            String username = matcher.group(1);
            String todosBlock = matcher.group(2);

            // 각 할 일 항목을 분리하여 리스트로 저장
            List<String> todos = Arrays.asList(todosBlock.split("\\d+\\. "));
            todos = new ArrayList<>(todos); // 수정 가능한 리스트로 변환
            todos.remove(0); // 첫 번째 빈 항목 제거

            gptTestResponses.add(new GPTTestResponse(username, todos));
        }
        return gptTestResponses;
    }
}
