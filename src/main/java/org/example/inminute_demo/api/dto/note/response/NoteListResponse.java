package org.example.inminute_demo.api.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoteListResponse<T> {

    private T notes;
}
