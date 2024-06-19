package org.example.inminute_demo.api.dto.note.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteRequest {

    private String name;
    private String script;
    private String summary;
}
