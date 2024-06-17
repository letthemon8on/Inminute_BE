package org.example.inminute_demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FolderListResponse<T> {

    private T folders;
}
