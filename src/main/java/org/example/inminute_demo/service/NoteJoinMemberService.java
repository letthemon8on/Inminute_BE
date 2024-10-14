package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.domain.NoteJoinMember;
import org.example.inminute_demo.dto.noteJoinMember.request.UpdateNoteJoinMemberRequest;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.repository.NoteJoinMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteJoinMemberService {

    private final NoteJoinMemberRepository noteJoinMemberRepository;

    @Transactional
    public void saveParents(Member member, Note note) {

        NoteJoinMember noteJoinMember = NoteJoinMember.builder()
                .member(member)
                .note(note)
                .build();
        noteJoinMemberRepository.save(noteJoinMember);
    }

    @Transactional
    public void updateNoteJoinMember(Long noteId, UpdateNoteJoinMemberRequest updateNoteJoinMemberRequest) {

        NoteJoinMember noteJoinMember = noteJoinMemberRepository.findByNote_Id(noteId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTE_NOT_FOUND));

        noteJoinMember.update(updateNoteJoinMemberRequest.getSummary(), updateNoteJoinMemberRequest.getTodo());
        noteJoinMemberRepository.save(noteJoinMember);
    }
}
