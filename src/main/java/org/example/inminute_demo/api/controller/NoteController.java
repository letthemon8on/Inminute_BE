package org.example.inminute_demo.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.api.dto.note.request.CreateNoteRequest;
import org.example.inminute_demo.api.dto.note.request.UpdateNoteRequest;
import org.example.inminute_demo.api.dto.note.response.CreateNoteResponse;
import org.example.inminute_demo.api.dto.note.response.NoteDetailResponse;
import org.example.inminute_demo.api.dto.note.response.NoteListResponse;
import org.example.inminute_demo.api.dto.note.response.UpdateNoteResponse;
import org.example.inminute_demo.api.service.NoteService;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.example.inminute_demo.global.login.dto.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Note", description = "Note 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/notes")
    public ApiResponse<CreateNoteResponse> createNote(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody CreateNoteRequest createNoteRequest) {
        return ApiResponse.onSuccess(noteService.createNote(customOAuth2User.getUsername(), createNoteRequest));
    }

    @PatchMapping("/notes/{noteId}")
    public ApiResponse<UpdateNoteResponse> updateNote(@PathVariable Long noteId, @RequestBody UpdateNoteRequest updateNoteRequest) {
        return ApiResponse.onSuccess(noteService.updateNote(noteId, updateNoteRequest));
    }

    @GetMapping("/notes/all")
    public ApiResponse<NoteListResponse> getNoteList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.onSuccess(noteService.getNoteList(customOAuth2User.getUsername()));
    }

    @GetMapping("/notes")
    public ApiResponse<NoteListResponse> getNoteListByFolder(@RequestParam Long folderId) {
        return ApiResponse.onSuccess(noteService.getNoteListByFolder(folderId));
    }

    @GetMapping("/notes-detail/{noteId}")
    public ApiResponse<NoteDetailResponse> getNote(@PathVariable Long noteId) {
        return ApiResponse.onSuccess(noteService.getNote(noteId));
    }

    @DeleteMapping("/notes/{noteId}")
    public ApiResponse<?> deleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
        return ApiResponse.onSuccess("회의록 삭제됨");
    }
}
