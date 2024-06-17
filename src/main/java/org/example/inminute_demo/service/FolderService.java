package org.example.inminute_demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.Converter.FolderConverter;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.Folder;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.dto.request.CreateFolderRequset;
import org.example.inminute_demo.dto.response.CreateFolderResponse;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.FolderRepository;
import org.example.inminute_demo.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CreateFolderResponse createGroup(CreateFolderRequset createFolderRequset) {

        Member member = memberRepository.findById(createFolderRequset.getMemberId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.SESSION_UNAUTHORIZED));

        Folder folder = Folder.builder()
                .member(member)
                .build();

        folderRepository.save(folder);
        CreateFolderResponse createFolderResponse = FolderConverter.toCreateGroupResponse(folder);
        return createFolderResponse;
    }
}
