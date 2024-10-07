package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.repository.NoteRepository;
import org.example.inminute_demo.apipayload.Handler.TempHandler;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.dto.zoom.request.ZoomMeetingDTO;
import org.example.inminute_demo.dto.zoom.request.ZoomMeetingSettingsDTO;
import org.example.inminute_demo.zoom.ZoomTokenRepository;
import org.example.inminute_demo.repository.ZoomMeetingRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ZoomService {

    private final ZoomTokenRepository zoomTokenRepository;
    private final ZoomMeetingRepository zoomMeetingRepository;
    private final NoteRepository noteRepository;

    public void createMeeting(Long noteId) throws IOException {

        Note note = noteRepository.findById(noteId)
                        .orElseThrow(() -> new TempHandler(ErrorStatus.NOTE_NOT_FOUND));

        ZoomMeetingDTO zoomMeetingDTO = new ZoomMeetingDTO();

        System.out.println("Request to create a Zoom meeting");

        // API URL 설정
        String apiUrl = "https://api.zoom.us/v2/users/" + "swp0927@gmail.com" + "/meetings";

        // 미팅 topic 설정
        zoomMeetingDTO.setTopic(note.getName());

        // 호스트 이메일 설정
        zoomMeetingDTO.setHost_email("swp0927@gmail.com");

        // 미팅 설정
        ZoomMeetingSettingsDTO settingsDTO = new ZoomMeetingSettingsDTO();
        settingsDTO.setJoin_before_host(false);
        settingsDTO.setParticipant_video(true);
        settingsDTO.setHost_video(false);
        settingsDTO.setAuto_recording("local");
        settingsDTO.setMute_upon_entry(true);
        zoomMeetingDTO.setSettings(settingsDTO);

        // REST 요청 설정
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + zoomTokenRepository.findById(0L).get().getAccessToken());
        headers.add("content-type", "application/json");

        HttpEntity<ZoomMeetingDTO> httpEntity = new HttpEntity<>(zoomMeetingDTO, headers);
        ResponseEntity<ZoomMeetingDTO> zEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, ZoomMeetingDTO.class);

        // 응답 처리
        if (zEntity.getStatusCodeValue() == 201) {
            System.out.println("Zoom meeting response: " + zEntity);
            zoomMeetingRepository.save(zEntity.getBody().toEntity());
        } else {
            System.out.println("Error while creating zoom meeting: " + zEntity.getStatusCode());
        }

        zoomMeetingDTO.setSettings(null);
    }
}