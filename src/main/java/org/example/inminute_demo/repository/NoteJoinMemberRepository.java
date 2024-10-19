package org.example.inminute_demo.repository;

import org.example.inminute_demo.domain.NoteJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteJoinMemberRepository extends JpaRepository<NoteJoinMember, Long> {

    Optional<NoteJoinMember> findByNote_Id(Long noteId);
    Optional<NoteJoinMember> findByMember_UsernameAndNote_Uuid(String username, String uuid);
    Boolean existsByMember_UsernameAndNote_Uuid(String username, String uuid);
}
