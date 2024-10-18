package org.example.inminute_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.service.FolderService;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.dto.folder.request.CreateFolderRequest;
import org.example.inminute_demo.dto.folder.request.UpdateFolderRequest;
import org.example.inminute_demo.dto.folder.response.CreateFolderResponse;
import org.example.inminute_demo.dto.folder.response.FolderListResponse;
import org.example.inminute_demo.dto.folder.response.UpdateFolderResponse;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Folder", description = "Folder 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    @Operation(summary = "폴더 생성", description = "회의록이 담길 폴더를 생성합니다.")
    public ApiResponse<CreateFolderResponse> createFolder(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                          @RequestBody CreateFolderRequest createFolderRequest) {
        return ApiResponse.onSuccess(folderService.createFolder(customOAuth2User, createFolderRequest));
    }

    @PatchMapping("/{folderId}")
    @Operation(summary = "폴더 이름 수정", description = "폴더 이름을 수정합니다.")
    public ApiResponse<UpdateFolderResponse> updateFolder(@PathVariable Long folderId, @RequestBody UpdateFolderRequest updateFolderRequest) {
        return ApiResponse.onSuccess(folderService.updateFolder(folderId, updateFolderRequest));
    }

    @GetMapping("/all")
    @Operation(summary = "폴더 리스트 조회(폴더 바)", description = "생성시간 오름차순으로 폴더 리스트 및 각 폴더에 해당하는 회의록 리스트를 조회하고," +
            "<br> 폴더가 지정되지 않은 회의록 리스트를 생성시간 오름차순으로 조회합니다.")
    public ApiResponse<FolderListResponse> getFolderList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.onSuccess(folderService.getFolderList(customOAuth2User));
    }

    @DeleteMapping("/{folderId}")
    @Operation(summary = "폴더 삭제", description = "폴더 및 해당 폴더의 모든 회의록을 삭제합니다.")
    public ApiResponse<?> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ApiResponse.onSuccess("폴더 삭제됨");
    }
}