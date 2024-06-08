package org.example.inminute_demo.zoom.controller;

import org.example.inminute_demo.zoom.domain.ZoomMeeting;
import org.example.inminute_demo.zoom.dto.request.ZoomMeetingDTO;
import org.example.inminute_demo.zoom.service.ZoomService;
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
public class ZoomController {

    @Autowired
    private ZoomService zoomService;

    @PostMapping("/create-meeting")
    public ResponseEntity<ZoomMeeting> createMeeting(@RequestBody ZoomMeetingDTO zoomMeetingDTO) {
        try {
            ZoomMeeting createdMeeting = zoomService.createMeeting(zoomMeetingDTO);
            return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}