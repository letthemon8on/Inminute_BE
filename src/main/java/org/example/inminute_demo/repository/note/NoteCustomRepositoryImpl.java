package org.example.inminute_demo.repository.note;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.domain.Note;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.inminute_demo.domain.QNote.note;
import static org.example.inminute_demo.domain.QNoteJoinMember.noteJoinMember;

@Repository
@RequiredArgsConstructor
public class NoteCustomRepositoryImpl implements NoteCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Note> findAllByMemberId(Long memberId) {
        return jpaQueryFactory
                .selectFrom(note)
                .leftJoin(noteJoinMember)
                .on(noteJoinMember.note.eq(note))
                .where(noteJoinMember.member.id.eq(memberId))
                .fetch();
    }
}
