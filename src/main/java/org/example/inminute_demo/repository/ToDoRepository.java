package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    List<ToDo> findAllByNoteJoinMember_Id(Long id);
}
