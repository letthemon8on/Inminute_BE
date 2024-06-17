package org.example.inminute_demo.Converter;

import org.example.inminute_demo.domain.Folder;
import org.example.inminute_demo.dto.response.CreateFolderResponse;

public class FolderConverter {

    public static CreateFolderResponse toCreateGroupResponse(Folder folder) {

        return CreateFolderResponse.builder()
                .id(folder.getId())
                .build();
    }
}
