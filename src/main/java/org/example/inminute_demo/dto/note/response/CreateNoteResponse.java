package org.example.inminute_demo.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteResponse {

    private Long id;
    private LocalDateTime createdAt;
    private UUID uuid;
}
