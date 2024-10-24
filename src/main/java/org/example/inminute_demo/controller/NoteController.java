package org.example.inminute_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.dto.note.request.CreateNoteRequest;
import org.example.inminute_demo.dto.note.request.UpdateNoteRequest;
import org.example.inminute_demo.dto.note.response.CreateNoteResponse;
import org.example.inminute_demo.dto.note.response.NoteDetailResponse;
import org.example.inminute_demo.dto.note.response.NoteListResponse;
import org.example.inminute_demo.dto.note.response.UpdateNoteResponse;
import org.example.inminute_demo.dto.noteJoinMember.request.UpdateNoteJoinMemberRequest;
import org.example.inminute_demo.service.NoteJoinMemberService;
import org.example.inminute_demo.service.NoteService;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Note", description = "Note 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final NoteJoinMemberService noteJoinMemberService;

    @PostMapping
    @Operation(summary = "회의록 생성", description = "folderId 값을 requestBody에 포함할 경우 폴더가 지정된 회의록이, " +
            "포함하지 않을 경우 폴더가 지정되지 않은 회의록이 생성됩니다.")
    public ApiResponse<CreateNoteResponse> createNote(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                      @RequestBody CreateNoteRequest createNoteRequest) {
        return ApiResponse.onSuccess(noteService.createNote(customOAuth2User, createNoteRequest));
    }

    @PatchMapping("/{noteId}")
    @Operation(summary = "회의록 수정", description = "회의록 이름, 전체 스크립트, 한 줄 요약 중 원하는 항목을 수정합니다.")
    public ApiResponse<UpdateNoteResponse> updateNote(@PathVariable Long noteId, @RequestBody UpdateNoteRequest updateNoteRequest) {
        return ApiResponse.onSuccess(noteService.updateNote(noteId, updateNoteRequest));
    }

    @PatchMapping("/join-member/{noteId}")
    @Operation(summary = "회의록 화자별 세부정보 수정", description = "화자별 요약, 화자별 todo 중 원하는 항목을 수정합니다.")
    public ApiResponse<?> updateNoteJoinMember(@PathVariable Long noteId,
                                               @RequestBody UpdateNoteJoinMemberRequest updateNoteJoinMemberRequest) {
        noteJoinMemberService.updateNoteJoinMember(noteId, updateNoteJoinMemberRequest);
        return ApiResponse.onSuccess("수정 완료됨");
    }

    @GetMapping("/all")
    @Operation(summary = "회의록 리스트 조회", description = "생성시간 오름차순으로 전체 회의록 리스트를 조회합니다. " +
            "</br> 메인페이지에서 사용하세요.")
    public ApiResponse<NoteListResponse> getNoteList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.onSuccess(noteService.getNoteList(customOAuth2User));
    }

    @GetMapping
    @Operation(summary = "폴더별 회의록 리스트 조회", description = "지정한 folderId에 해당하는 회의록 리스트를 생성시간 오름차순으로 조회합니다. " +
            "</br> 폴더바에서 폴더 클릭 시 사용하세요.")
    public ApiResponse<NoteListResponse> getNoteListByFolder(@RequestParam Long folderId) {
        return ApiResponse.onSuccess(noteService.getNoteListByFolder(folderId));
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "회의록 상세정보 조회(개발중)", description = "지정한 회의록 상세 정보를 조회합니다.")
    public ApiResponse<NoteDetailResponse> getNote(@PathVariable Long noteId) {
        return ApiResponse.onSuccess(noteService.getNote(noteId));
    }

    @GetMapping("/detail/{uuid}")
    @Operation(summary = "UUID 회의록 상세정보 조회(개발중)", description = "url의 UUID를 통해 회의록 상세 정보를 조회합니다.")
    public ApiResponse<NoteDetailResponse> getNoteByUuid(@PathVariable String uuid) {
        return ApiResponse.onSuccess(noteService.getNoteByUuid(uuid));
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "회의록 삭제(개발중)", description = "지정한 회의록을 삭제합니다.")
    public ApiResponse<?> deleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
        return ApiResponse.onSuccess("회의록 삭제됨");
    }
}
