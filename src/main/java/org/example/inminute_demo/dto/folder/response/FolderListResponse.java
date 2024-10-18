package org.example.inminute_demo.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.inminute_demo.dto.note.response.NotesNotInFolder;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderListResponse {

    private List<FolderResponse> folders;
    private List<NotesNotInFolder> notes;
}
