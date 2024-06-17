package org.example.inminute_demo.api.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FolderListResponse<T> {

    private T folders;
}
