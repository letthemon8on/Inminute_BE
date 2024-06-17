package org.example.inminute_demo.dto.request;

import lombok.Getter;

@Getter
public class CreateFolderRequset {

    private Long memberId;
    private String name;

    public CreateFolderRequset(Long memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }
}
