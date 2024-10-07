package org.example.inminute_demo.dto.zoom.request;

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
}