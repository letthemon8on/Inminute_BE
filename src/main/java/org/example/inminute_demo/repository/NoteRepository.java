package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
