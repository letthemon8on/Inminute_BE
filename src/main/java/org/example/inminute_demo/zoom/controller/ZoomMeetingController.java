package org.example.inminute_demo.zoom.controller;

import org.example.inminute_demo.zoom.domain.ZoomMeetingObjectEntity;
import org.example.inminute_demo.zoom.dto.ZoomMeetingObjectDTO;
import org.example.inminute_demo.zoom.service.ZoomMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/zoom")
public class ZoomMeetingController {

    @Autowired
    private ZoomMeetingService zoomMeetingService;

    @PostMapping("/create-meeting")
    public ResponseEntity<ZoomMeetingObjectEntity> createMeeting(@RequestBody ZoomMeetingObjectDTO zoomMeetingObjectDTO) {
        try {
            ZoomMeetingObjectEntity createdMeeting = zoomMeetingService.createMeeting(zoomMeetingObjectDTO);
            return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}