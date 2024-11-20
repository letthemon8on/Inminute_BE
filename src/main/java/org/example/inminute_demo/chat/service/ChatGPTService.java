package org.example.inminute_demo.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.chat.config.ChatGPTConfig;
import org.example.inminute_demo.chat.dto.request.GPTRequest;
import org.example.inminute_demo.chat.dto.request.QuestionRequest;
import org.example.inminute_demo.chat.dto.request.ToDoRequest;
import org.example.inminute_demo.chat.dto.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.prompt-url}")
    private String promptUrl;

    public List<ToDoResponse> todo(String prompt) {

        HttpHeaders headers = chatGPTConfig.httpHeaders();

        GPTRequest gptRequest = new GPTRequest(model, prompt);

        HttpEntity<GPTRequest> request = new HttpEntity<>(gptRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        GPTResponse gptResponse = restTemplate.postForObject(promptUrl, request, GPTResponse.class);

        if (gptResponse == null || gptResponse.getChoices() == null || gptResponse.getChoices().isEmpty()) {
            throw new RuntimeException();
        }

        System.out.println(gptResponse.getChoices().get(0).getMessage().getContent());

        List<ToDoResponse> toDoResponses = extractTodos(gptResponse.getChoices().get(0).getMessage().getContent());

        return toDoResponses;
    }

    public List<ToDoResponse> extractTodos(String input) {

        // 각 사용자와 할 일 목록을 매칭하기 위한 정규식
        Pattern pattern = Pattern.compile("- ([가-힣a-zA-Z0-9]+): ((?:\\d+\\. [^\\d]+)+)");
        Matcher matcher = pattern.matcher(input);

        List<ToDoResponse> toDoResponses = new ArrayList<>();
        while (matcher.find()) {
            String nickname = matcher.group(1); // 사용자 닉네임 추출
            String todosBlock = matcher.group(2); // 할 일 블록 추출

            // 각 할 일 항목을 숫자와 점("1.", "2.")으로 분리
            List<ToDoList> toDoLists = Arrays.stream(todosBlock.split("\\d+\\. "))
                    .filter(todo -> !todo.isBlank()) // 빈 문자열 필터링
                    .map(todo -> todo.replace("\n", "").trim()) // 줄바꿈과 공백 제거
                    .map(ToDoList::new) // ToDoList 객체로 매핑
                    .collect(Collectors.toList());

            // ToDoResponse 객체 생성 및 추가
            toDoResponses.add(new ToDoResponse(nickname, toDoLists));
        }
        return toDoResponses;
    }

    public AnswerResponse question(String prompt) {

        HttpHeaders headers = chatGPTConfig.httpHeaders();

        GPTRequest gptRequest = new GPTRequest(model, prompt);

        HttpEntity<GPTRequest> request = new HttpEntity<>(gptRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        GPTResponse gptResponse = restTemplate.postForObject(promptUrl, request, GPTResponse.class);

        if (gptResponse == null || gptResponse.getChoices() == null || gptResponse.getChoices().isEmpty()) {
            throw new RuntimeException();
        }

        System.out.println(gptResponse.getChoices().get(0).getMessage().getContent());

        return new AnswerResponse(gptResponse.getChoices().get(0).getMessage().getContent());
    }
}
