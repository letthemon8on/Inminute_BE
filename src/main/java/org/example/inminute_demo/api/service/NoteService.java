package org.example.inminute_demo.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.api.Converter.NoteConverter;
import org.example.inminute_demo.api.domain.Folder;
import org.example.inminute_demo.api.domain.Note;
import org.example.inminute_demo.api.domain.Participant;
import org.example.inminute_demo.api.dto.note.request.CreateNoteRequest;
import org.example.inminute_demo.api.dto.note.request.UpdateNoteRequest;
import org.example.inminute_demo.api.dto.note.response.*;
import org.example.inminute_demo.api.repository.FolderRepository;
import org.example.inminute_demo.api.repository.NoteRepository;
import org.example.inminute_demo.api.repository.ParticipantRepository;
import org.example.inminute_demo.global.apipayload.Handler.TempHandler;
import org.example.inminute_demo.global.apipayload.code.status.ErrorStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final FolderRepository folderRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public CreateNoteResponse createNote(CreateNoteRequest createNoteRequest) {

        Folder folder = folderRepository.findByName(createNoteRequest.getFolderName());

        Note note = Note.builder()
                .folder(folder)
                .name(createNoteRequest.getName())
                .build();

        noteRepository.save(note);
        CreateNoteResponse createNoteResponse = NoteConverter.toCreateNoteResponse(note);
        return createNoteResponse;
    }

    public UpdateNoteResponse updateNote(Long noteId, UpdateNoteRequest updateNoteRequest) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.NOTE_NOT_FOUND));

        note.update(updateNoteRequest.getName(), updateNoteRequest.getScript(), updateNoteRequest.getSummary());
        noteRepository.save(note);

        UpdateNoteResponse updateNoteResponse = NoteConverter.toUpdateNoteResponse(note);
        return updateNoteResponse;
    }

    public NoteListResponse getNoteList() {

        List<Note> notes = noteRepository.findAll();
        List<NoteResponse> noteResponses = new ArrayList<>();

        for (Note note : notes) {
            NoteResponse noteResponse = NoteResponse.builder()
                    .id(note.getId())
                    .name(note.getName())
                    .createdAt(note.getCreated_at())
                    .summary(note.getSummary())
                    .build();

            noteResponses.add(noteResponse);
        }

        return new NoteListResponse(noteResponses);
    }

    public NoteListResponse getNoteListByFolder(Long folderId) {

        List<Note> notes = noteRepository.findAllByFolder_Id(folderId);
        List<NoteResponse> noteResponses = new ArrayList<>();

        for (Note note : notes) {
            NoteResponse noteResponse = NoteResponse.builder()
                    .id(note.getId())
                    .name(note.getName())
                    .createdAt(note.getCreated_at())
                    .summary(note.getSummary())
                    .build();

            noteResponses.add(noteResponse);
        }

        return new NoteListResponse(noteResponses);
    }

    public NoteDetailResponse getNote(Long noteId) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.NOTE_NOT_FOUND));

        List<Participant> participants = participantRepository.findAllByNote_Id(noteId);
        List<ParticipantName> participantNames = new ArrayList<>();

        for (Participant participant : participants) {
            ParticipantName participantName = ParticipantName.builder()
                    .name(participant.getName())
                    .build();

            participantNames.add(participantName);
        }

        NoteDetailResponse noteDetailResponse = NoteDetailResponse.builder()
                .id(note.getId())
                .name(note.getName())
                .script(note.getScript())
                .summary(note.getSummary())
                .participantNames(participantNames)
                .build();

        return noteDetailResponse;
    }

    @Transactional
    public void deleteNote(Long noteId) {

        noteRepository.deleteById(noteId);
    }
}
