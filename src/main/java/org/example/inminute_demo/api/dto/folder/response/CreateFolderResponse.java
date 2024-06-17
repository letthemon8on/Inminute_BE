package org.example.inminute_demo.api.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CreateFolderResponse {

    private Long id;
    private LocalDateTime createdAt;
}
