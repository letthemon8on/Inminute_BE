package org.example.inminute_demo.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.dto.flask.request.SummaryRequest;
import org.example.inminute_demo.chat.dto.flask.response.SummaryResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SummaryService {

    //데이터를 JSON 객체로 변환하기 위해서 사용
    private final ObjectMapper objectMapper;
    private final String FLASK_URL = "http://inminute_flask:5000/summary";

    @Transactional
    public SummaryResponse getSummaryFromFlask(SummaryRequest summaryRequest) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        // 요청을 JSON으로 변환
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 객체를 JSON 문자열로 변환
        String param = objectMapper.writeValueAsString(summaryRequest);
        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        // Flask 서버에 POST 요청 전송
        ResponseEntity<SummaryResponse> response = restTemplate.postForEntity(FLASK_URL, entity, SummaryResponse.class);

        // 응답에서 summary 필드값 반환
        if (response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }
}
