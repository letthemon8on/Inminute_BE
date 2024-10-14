package org.example.inminute_demo.service;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.domain.Note;
import org.example.inminute_demo.domain.NoteJoinMember;
import org.example.inminute_demo.repository.NoteJoinMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteJoinMemberService {

    private final NoteJoinMemberRepository noteJoinMemberRepository;

    @Transactional
    public void saveParents(Member member, Note note) {

        NoteJoinMember noteJoinMember = NoteJoinMember.builder() // 중간 테이블 엔티티 생성
                .member(member)
                .note(note)
                .build();
        noteJoinMemberRepository.save(noteJoinMember);
    }
}
