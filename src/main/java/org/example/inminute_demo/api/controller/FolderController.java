package org.example.inminute_demo.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.api.service.FolderService;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.example.inminute_demo.api.dto.folder.request.CreateFolderRequest;
import org.example.inminute_demo.api.dto.folder.request.UpdateFolderRequest;
import org.example.inminute_demo.api.dto.folder.response.CreateFolderResponse;
import org.example.inminute_demo.api.dto.folder.response.FolderListResponse;
import org.example.inminute_demo.api.dto.folder.response.UpdateFolderResponse;
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
        System.out.println("Received request: " + updateFolderRequest);
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