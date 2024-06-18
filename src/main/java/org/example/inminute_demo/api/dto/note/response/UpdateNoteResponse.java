package org.example.inminute_demo.api.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UpdateNoteResponse {

    private Long id;
    private LocalDateTime updatedAt;
}
