package org.example.inminute_demo.api.Converter;

import org.example.inminute_demo.api.domain.Note;
import org.example.inminute_demo.api.dto.note.response.CreateNoteResponse;
import org.example.inminute_demo.api.dto.note.response.NoteDetailResponse;
import org.example.inminute_demo.api.dto.note.response.UpdateNoteResponse;

public class NoteConverter {

    public static CreateNoteResponse toCreateNoteResponse(Note note) {

        return CreateNoteResponse.builder()
                .id(note.getId())
                .createdAt(note.getCreated_at())
                .build();
    }

    public static UpdateNoteResponse toUpdateNoteResponse(Note note) {

        return UpdateNoteResponse.builder()
                .id(note.getId())
                .updatedAt(note.getUpdated_at())
                .build();
    }
}
