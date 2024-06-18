package org.example.inminute_demo.api.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NoteResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private String summary;
}
