package org.example.inminute_demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.dto.request.CreateFolderRequset;
import org.example.inminute_demo.dto.response.CreateFolderResponse;
import org.example.inminute_demo.service.FolderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ApiResponse<CreateFolderResponse> createGroup(@RequestBody CreateFolderRequset createFolderRequset) {
        return ApiResponse.onSuccess(folderService.createGroup(createFolderRequset));
    }


}
