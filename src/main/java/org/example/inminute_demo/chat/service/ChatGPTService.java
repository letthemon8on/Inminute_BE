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
        Pattern pattern = Pattern.compile("- ([가-힣a-zA-Z0-9]+): ((?:\\d+\\. .+? ?)+)");
        Matcher matcher = pattern.matcher(input);

        List<ToDoResponse> toDoResponses = new ArrayList<>();
        while (matcher.find()) {
            String nickname = matcher.group(1);
            String todosBlock = matcher.group(2);

            // 각 할 일 항목을 분리하여 리스트로 저장
            List<String> todos = Arrays.asList(todosBlock.split("\\d+\\. "));
            todos = new ArrayList<>(todos); // 수정 가능한 리스트로 변환
            todos.remove(0); // 첫 번째 빈 항목 제거

            // 각 할 일 항목에서 "\n" 제거
            todos.replaceAll(todo -> todo.replace("\n", "").trim());

            List<ToDoList> toDoLists = todos.stream()
                    .map(ToDoList::new)
                    .collect(Collectors.toList());

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
