package org.example.inminute_demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.dto.request.CreateFolderRequest;
import org.example.inminute_demo.dto.request.UpdateFolderRequest;
import org.example.inminute_demo.dto.response.CreateFolderResponse;
import org.example.inminute_demo.dto.response.FolderListResponse;
import org.example.inminute_demo.dto.response.UpdateFolderResponse;
import org.example.inminute_demo.service.FolderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ApiResponse<CreateFolderResponse> createFolder(@RequestBody CreateFolderRequest createFolderRequest) {
        return ApiResponse.onSuccess(folderService.createFolder(createFolderRequest));
    }

    // 400 에러 수정 필요
    @PatchMapping("/{folderId}")
    public ApiResponse<UpdateFolderResponse> updateFolder(@PathVariable Long folderId, @RequestBody UpdateFolderRequest updateFolderRequest) {
        return ApiResponse.onSuccess(folderService.updateFolder(folderId, updateFolderRequest));
    }

    @GetMapping
    public ApiResponse<FolderListResponse> getFolderList() {
        return ApiResponse.onSuccess(folderService.getFolderList());
    }

    @DeleteMapping("/{folderId}")
    public ApiResponse<?> deleteFolder(@PathVariable Long folderId) {
        folderService.deleteFolder(folderId);
        return ApiResponse.onSuccess("폴더 삭제됨");
    }
}