package org.example.inminute_demo.zoom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.example.inminute_demo.zoom.domain.ZoomMeetingObjectEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ZoomMeetingObjectDTO {
    private String topic;
    // private int duration;
    // private String start_time;
    private String host_email;
    private ZoomMeetingSettingsDTO settings;

    // Getters and setters

    public ZoomMeetingObjectEntity toEntity() {
        ZoomMeetingObjectEntity entity = new ZoomMeetingObjectEntity();
        entity.setTopic(this.topic);
        // entity.setDuration(this.duration);
        // entity.setStartTime(this.start_time);
        entity.setHostEmail(this.host_email);
        // Set other fields if needed
        return entity;
    }
}