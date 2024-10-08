package org.example.inminute_demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.converter.NoteConverter;
import org.example.inminute_demo.domain.Folder;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.dto.note.request.CreateNoteRequest;
import org.example.inminute_demo.dto.note.request.UpdateNoteRequest;
import org.example.inminute_demo.repository.FolderRepository;
import org.example.inminute_demo.repository.NoteRepository;
import org.example.inminute_demo.apipayload.Handler.TempHandler;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.dto.note.response.*;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CreateNoteResponse createNote(String username, CreateNoteRequest createNoteRequest) {

        Member member= memberRepository.findByUsername(username);
        Folder folder = folderRepository.findById(createNoteRequest.getFolderId())
                .orElseThrow(() -> new TempHandler(ErrorStatus.FOLDER_NOT_FOUND));

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

    public NoteListResponse getNoteList(String username) {

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

        NoteDetailResponse noteDetailResponse = NoteDetailResponse.builder()
                .id(note.getId())
                .name(note.getName())
                .script(note.getScript())
                .summary(note.getSummary())
                .createdAt(note.getCreated_at())
                .build();

        return noteDetailResponse;
    }

    @Transactional
    public void deleteNote(Long noteId) {

        Note note = noteRepository.findById(noteId)
                        .orElseThrow(() -> new TempHandler(ErrorStatus.NOTE_NOT_FOUND));

        noteRepository.delete(note);
    }
}
