package org.example.inminute_demo.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private String summary;
}
