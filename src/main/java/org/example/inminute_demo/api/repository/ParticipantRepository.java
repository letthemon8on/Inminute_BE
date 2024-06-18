package org.example.inminute_demo.api.repository;

import org.example.inminute_demo.api.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByNote_Id(Long noteId);
}
