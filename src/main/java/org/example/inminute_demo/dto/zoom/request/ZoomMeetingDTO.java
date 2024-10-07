package org.example.inminute_demo.dto.zoom.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.example.inminute_demo.domain.ZoomMeeting;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ZoomMeetingDTO {
    private String topic;
    // private int duration;
    // private String start_time;
    private String host_email;
    private ZoomMeetingSettingsDTO settings;

    // Getters and setters

    public ZoomMeeting toEntity() {
        ZoomMeeting entity = new ZoomMeeting();
        entity.setTopic(this.topic);
        // entity.setDuration(this.duration);
        // entity.setStartTime(this.start_time);
        entity.setHostEmail(this.host_email);
        // Set other fields if needed
        return entity;
    }
}