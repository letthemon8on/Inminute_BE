package org.example.inminute_demo.api.dto.folder.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateFolderRequest {

    private Long memberId;
    private String name;
}
