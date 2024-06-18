package org.example.inminute_demo.api.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NoteDetailResponse {

    private Long id;
    private String name;
    private String script;
    private String summary;
    private List<String> participants;
}
