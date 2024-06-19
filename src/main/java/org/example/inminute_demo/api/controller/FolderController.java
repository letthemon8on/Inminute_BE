package org.example.inminute_demo.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.api.service.FolderService;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.example.inminute_demo.api.dto.folder.request.CreateFolderRequest;
import org.example.inminute_demo.api.dto.folder.request.UpdateFolderRequest;
import org.example.inminute_demo.api.dto.folder.response.CreateFolderResponse;
import org.example.inminute_demo.api.dto.folder.response.FolderListResponse;
import org.example.inminute_demo.api.dto.folder.response.UpdateFolderResponse;
import org.example.inminute_demo.global.login.dto.CustomOAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Folder", description = "Folder 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ApiResponse<CreateFolderResponse> createFolder(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody CreateFolderRequest createFolderRequest) {
        return ApiResponse.onSuccess(folderService.createFolder(customOAuth2User.getUsername(), createFolderRequest));
    }

    @PatchMapping("/{folderId}")
    public ApiResponse<UpdateFolderResponse> updateFolder(@PathVariable Long folderId, @RequestBody UpdateFolderRequest updateFolderRequest) {
        return ApiResponse.onSuccess(folderService.updateFolder(folderId, updateFolderRequest));
    }

    @GetMapping("/all")
    public ApiResponse<FolderListResponse> getFolderList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.onSuccess(folderService.getFolderList(customOAuth2User.getUsername()));
    }

    @DeleteMapping("/{folderId}")
    public ApiResponse<?> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ApiResponse.onSuccess("폴더 삭제됨");
    }
}