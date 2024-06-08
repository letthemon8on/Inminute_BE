package org.example.inminute_demo.zoom.service;

import org.example.inminute_demo.zoom.domain.ZoomMeetingObjectEntity;
import org.example.inminute_demo.zoom.dto.ZoomMeetingObjectDTO;
import org.example.inminute_demo.zoom.dto.ZoomMeetingSettingsDTO;
import org.example.inminute_demo.zoom.oauth2.ZoomTokenRepository;
import org.example.inminute_demo.zoom.repository.ZoomMeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ZoomMeetingService {

    @Autowired
    private ZoomTokenRepository zoomTokenRepository;

    @Autowired
    private ZoomMeetingRepository zoomMeetingRepository;

    public ZoomMeetingObjectEntity createMeeting(ZoomMeetingObjectDTO zoomMeetingObjectDTO) throws IOException {
        // Token 만료 여부 확인
        // isExpired();

        System.out.println("Request to create a Zoom meeting");

        // API URL 설정
        String apiUrl = "https://api.zoom.us/v2/users/" + "swp0927@gmail.com" + "/meetings";

        // 호스트 이메일 설정
        zoomMeetingObjectDTO.setHost_email("swp0927@gmail.com");

        // 미팅 설정
        ZoomMeetingSettingsDTO settingsDTO = new ZoomMeetingSettingsDTO();
        settingsDTO.setJoin_before_host(false);
        settingsDTO.setParticipant_video(true);
        settingsDTO.setHost_video(false);
        settingsDTO.setAuto_recording("local");
        settingsDTO.setMute_upon_entry(true);
        zoomMeetingObjectDTO.setSettings(settingsDTO);

        // REST 요청 설정
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + zoomTokenRepository.findById(0L).get().getAccessToken());
        headers.add("content-type", "application/json");

        HttpEntity<ZoomMeetingObjectDTO> httpEntity = new HttpEntity<>(zoomMeetingObjectDTO, headers);
        ResponseEntity<ZoomMeetingObjectDTO> zEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, ZoomMeetingObjectDTO.class);

        // 응답 처리
        if (zEntity.getStatusCodeValue() == 201) {
            System.out.println("Zoom meeting response: " + zEntity);
            return zoomMeetingRepository.save(zEntity.getBody().toEntity());
        } else {
            System.out.println("Error while creating zoom meeting: " + zEntity.getStatusCode());
        }

        zoomMeetingObjectDTO.setSettings(null);
        return null;
    }
}