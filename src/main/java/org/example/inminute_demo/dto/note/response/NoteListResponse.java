package org.example.inminute_demo.dto.note.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoteListResponse<T> {

    private T notes;
}
