package org.example.inminute_demo.zoom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoomMeetingSettingsDTO {
    private boolean join_before_host;
    private boolean participant_video;
    private boolean host_video;
    private String auto_recording;
    private boolean mute_upon_entry;

    // Getters and setters
}