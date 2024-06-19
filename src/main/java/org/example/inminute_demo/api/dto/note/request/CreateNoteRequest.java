package org.example.inminute_demo.api.dto.note.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteRequest {

    private String name;
    private String folderName;
}
