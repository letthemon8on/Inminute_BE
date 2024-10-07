package org.example.inminute_demo.converter;

import org.example.inminute_demo.domain.Folder;
import org.example.inminute_demo.dto.folder.response.CreateFolderResponse;
import org.example.inminute_demo.dto.folder.response.UpdateFolderResponse;

public class FolderConverter {

    public static CreateFolderResponse toCreateGroupResponse(Folder folder) {

        return CreateFolderResponse.builder()
                .id(folder.getId())
                .createdAt(folder.getCreated_at())
                .build();
    }

    public static UpdateFolderResponse toUpdateFolderResponse(Folder folder) {

        return UpdateFolderResponse.builder()
                .id(folder.getId())
                .updatedAt(folder.getUpdated_at())
                .build();
    }
}
