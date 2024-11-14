package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.domain.NoteJoinMember;
import org.example.inminute_demo.dto.noteJoinMember.request.UpdateNoteJoinMemberRequest;
import org.example.inminute_demo.dto.noteJoinMember.response.NoteJoinMemberListResponse;
import org.example.inminute_demo.dto.noteJoinMember.response.NoteJoinMemberResponse;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.NoteJoinMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteJoinMemberService {

    private final NoteJoinMemberRepository noteJoinMemberRepository;
    private final ToDoService toDoService;

    @Transactional
    public void saveParents(Member member, Note note) {

        NoteJoinMember noteJoinMember = NoteJoinMember.builder()
                .member(member)
                .note(note)
                .build();
        noteJoinMemberRepository.save(noteJoinMember);
    }

    @Transactional
    public void updateNoteJoinMemberSummary(Long noteJoinMemberId, UpdateNoteJoinMemberRequest updateNoteJoinMemberRequest) {

        NoteJoinMember noteJoinMember = noteJoinMemberRepository.findById(noteJoinMemberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTE_NOT_FOUND));

        noteJoinMember.updateSummary(updateNoteJoinMemberRequest.summary());
        noteJoinMemberRepository.save(noteJoinMember);
    }

    public List<NoteJoinMember> getAllNoteJoinMemberByNoteId(Long noteId) {

        return noteJoinMemberRepository.findAllByNote_Id(noteId);
    }

    @Transactional
    public void updateSummary(String uuid, String username, String summary) {

        NoteJoinMember noteJoinMember = noteJoinMemberRepository.findByMember_UsernameAndNote_Uuid(username, uuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTE_NOT_FOUND));

        noteJoinMember.updateSummary(summary);
        noteJoinMemberRepository.save(noteJoinMember);
    }

    public NoteJoinMemberListResponse getAllNoteJoinMember(String uuid) {

        List<NoteJoinMember> noteJoinMembers = noteJoinMemberRepository.findAllByNote_Uuid(uuid);

        List<NoteJoinMemberResponse> noteJoinMemberResponses = noteJoinMembers.stream()
                .map(noteJoinMember -> NoteJoinMemberResponse.builder()
                        .id(noteJoinMember.getId())
                        .nickname(noteJoinMember.getMember().getNickname())
                        .summary(noteJoinMember.getSummary())
                        .toDoList(toDoService.findAll(noteJoinMember.getId()))
                        .build())
                .collect(Collectors.toList());

        return new NoteJoinMemberListResponse(noteJoinMemberResponses);
    }
}
