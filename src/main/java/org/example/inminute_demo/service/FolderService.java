package org.example.inminute_demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.converter.FolderConverter;
import org.example.inminute_demo.repository.FolderRepository;
import org.example.inminute_demo.repository.NoteRepository;
import org.example.inminute_demo.apipayload.Handler.TempHandler;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.Folder;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.dto.folder.request.CreateFolderRequest;
import org.example.inminute_demo.dto.folder.request.UpdateFolderRequest;
import org.example.inminute_demo.dto.folder.response.*;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final MemberService memberService;

    @Transactional
    public CreateFolderResponse createFolder(CustomOAuth2User customOAuth2User, CreateFolderRequest createFolderRequest) {

        Member member = memberService.loadMemberByCustomOAuth2User(customOAuth2User);

        Folder folder = Folder.builder()
                .member(member)
                .name(createFolderRequest.getName())
                .build();

        folderRepository.save(folder);

        CreateFolderResponse createFolderResponse = FolderConverter.toCreateGroupResponse(folder);
        return createFolderResponse;
    }

    @Transactional
    public UpdateFolderResponse updateFolder(Long folderId, UpdateFolderRequest updateFolderRequest) {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.FOLDER_NOT_FOUND));

        folder.update(updateFolderRequest.getName());
        folderRepository.save(folder);

        UpdateFolderResponse updateFolderResponse = FolderConverter.toUpdateFolderResponse(folder);
        return updateFolderResponse;
    }

    public FolderListResponse getFolderList(String username) {

        List<Folder> folders = folderRepository.findAllByMember_Username(username);
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

        Folder folder = folderRepository.findById(folderId)
                        .orElseThrow(() -> new TempHandler(ErrorStatus.FOLDER_NOT_FOUND));

        folderRepository.delete(folder);
    }
}
