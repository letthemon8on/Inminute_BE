package org.example.inminute_demo.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDetailResponse {

    private Long id;
    private String name;
    private String script;
    private String summary;
    private LocalDateTime createdAt;
}
