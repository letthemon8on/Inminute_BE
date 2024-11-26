package org.example.inminute_demo.repository.note;

import org.example.inminute_demo.domain.Note;

import java.util.List;

public interface NoteCustomRepository {

    List<Note> findAllByMemberId(Long memberId);
    List<Note> findALlByMemberIdAndFolderIsNull(Long memberId);
}
