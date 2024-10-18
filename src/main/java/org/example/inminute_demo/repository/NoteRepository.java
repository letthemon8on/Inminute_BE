package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByFolder_Id(Long folderId);
    List<Note> findAllByMember_Id(Long memberId);

    Optional<Note> findByUuid(UUID uuid);
}
