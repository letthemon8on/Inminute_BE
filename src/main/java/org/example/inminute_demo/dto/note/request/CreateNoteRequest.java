package org.example.inminute_demo.dto.note.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoteRequest {

    private String name;
    private Long folderId;
}
