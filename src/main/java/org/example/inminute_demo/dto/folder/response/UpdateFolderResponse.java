package org.example.inminute_demo.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFolderResponse {

    private Long id;
    private LocalDateTime updatedAt;
}