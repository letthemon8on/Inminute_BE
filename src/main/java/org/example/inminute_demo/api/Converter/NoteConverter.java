package org.example.inminute_demo.api.Converter;

import org.example.inminute_demo.api.domain.Note;
import org.example.inminute_demo.api.dto.note.response.CreateNoteResponse;
import org.example.inminute_demo.api.dto.note.response.NoteDetailResponse;

public class NoteConverter {

    public static CreateNoteResponse toCreateNoteResponse(Note note) {

        return CreateNoteResponse.builder()
                .id(note.getId())
                .createdAt(note.getCreated_at())
                .build();
    }

    public static NoteDetailResponse toNoteDetailResponse(Note note) {

        return NoteDetailResponse.builder()
                .id(note.getId())
                .name((note.getName()))
                .script(note.getScript())
                .summary(note.getSummary())
                .participants(note.getParticipants())
                .build();
    }
}
