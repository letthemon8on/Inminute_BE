package org.example.inminute_demo.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderResponse {

    private Long id;
    private String name;
    private LocalDateTime create_at;
    private List<NotesInFolder> notesInFolders;
}
