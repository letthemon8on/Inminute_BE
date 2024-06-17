package org.example.inminute_demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.Converter.FolderConverter;
import org.example.inminute_demo.apipayload.Handler.TempHandler;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.Folder;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.dto.request.CreateFolderRequest;
import org.example.inminute_demo.dto.request.UpdateFolderRequest;
import org.example.inminute_demo.dto.response.*;
import org.example.inminute_demo.repository.FolderRepository;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;

    @Transactional
    public CreateFolderResponse createFolder(CreateFolderRequest createFolderRequest) {

        Member member = memberRepository.findById(createFolderRequest.getMemberId())
                .orElseThrow(() -> new TempHandler(ErrorStatus.SESSION_UNAUTHORIZED));

        Folder folder = Folder.builder()
                .member(member)
                .name(createFolderRequest.getName())
                .build();

        folderRepository.save(folder);
        CreateFolderResponse createFolderResponse = FolderConverter.toCreateGroupResponse(folder);
        return createFolderResponse;
    }

    // 400 에러 수정 필요
    @Transactional
    public UpdateFolderResponse updateFolder(Long folderId, UpdateFolderRequest updateFolderRequest) {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.FOLDER_NOT_FOUND));

        folder.update(updateFolderRequest.getName());
        UpdateFolderResponse updateFolderResponse = FolderConverter.toUpdateFolderResponse(folder);
        return updateFolderResponse;
    }

    public FolderListResponse getFolderList() {

        List<Folder> folders = folderRepository.findAll();
        List<FolderResponse> folderResponses = new ArrayList<>();

        for (Folder folder : folders) {
            List<Note> notes = noteRepository.findAllByFolder_Id(folder.getId());
            List<NotesInFolder> notesInFolders = new ArrayList<>();

            for (Note note : notes) {
                NotesInFolder notesInFolder = NotesInFolder.builder()
                        .id(note.getId())
                        .name(note.getName())
                        .createdAt(note.getCreated_at())
                        .build();

                notesInFolders.add(notesInFolder);
            }

            FolderResponse folderResponse = FolderResponse.builder()
                    .id(folder.getId())
                    .name(folder.getName())
                    .notesInFolders(notesInFolders)
                    .build();

            folderResponses.add(folderResponse);
        }

        return new FolderListResponse(folderResponses);
    }

    @Transactional
    public void deleteFolder(Long folderId) {

        folderRepository.deleteById(folderId);
    }
}
