package org.example.inminute_demo.api.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FolderListResponse<T> {

    private T folders;
}
