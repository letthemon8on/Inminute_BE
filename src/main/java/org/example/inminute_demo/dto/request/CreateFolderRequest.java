package org.example.inminute_demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateFolderRequest {

    private Long memberId;
    private String name;
}
