package org.example.inminute_demo.converter;

import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.dto.note.response.CreateNoteResponse;
import org.example.inminute_demo.dto.note.response.UpdateNoteResponse;

public class NoteConverter {

    public static CreateNoteResponse toCreateNoteResponse(Note note) {

        return CreateNoteResponse.builder()
                .id(note.getId())
                .createdAt(note.getCreated_at())
                .uuid(note.getUuid())
                .build();
    }

    public static UpdateNoteResponse toUpdateNoteResponse(Note note) {

        return UpdateNoteResponse.builder()
                .id(note.getId())
                .updatedAt(note.getUpdated_at())
                .build();
    }
}
