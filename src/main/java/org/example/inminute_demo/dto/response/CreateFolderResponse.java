package org.example.inminute_demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateFolderResponse {

    private Long id;

    public CreateFolderResponse(Long id) {
        this.id = id;
    }
}
