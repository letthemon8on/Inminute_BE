package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByFolder_Id(Long folderId);
    List<Note> findAllByUserEntity_Username(String username);
}
