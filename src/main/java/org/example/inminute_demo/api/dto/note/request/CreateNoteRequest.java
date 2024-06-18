package org.example.inminute_demo.api.dto.note.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateNoteRequest {

    private String name;
    private String folderName;
}
