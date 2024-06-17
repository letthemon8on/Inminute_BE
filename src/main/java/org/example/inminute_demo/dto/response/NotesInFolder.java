package org.example.inminute_demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotesInFolder {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
