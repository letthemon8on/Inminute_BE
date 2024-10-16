package org.example.inminute_demo.chat.repository;

import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.dto.response.ChatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // DTO로 반환
    @Query("""
			SELECT 
			new org.example.inminute_demo.chat.dto.response.ChatResponse
			(c.id, u.id, u.nickname, c.type, c.created_at, c.content) 
			FROM Chat c 
			JOIN Member u ON u.id = c.memberId 
			WHERE c.noteId = :noteId
		""")
    Page<ChatResponse> findByNoteId(@Param("noteId") Long noteId, Pageable pageable);

    @Query("""
			SELECT 
			new org.example.inminute_demo.chat.dto.response.ChatResponse
			(c.id, u.id, u.nickname, c.type, c.created_at, c.content) 
			FROM Chat c 
			JOIN Member u ON u.id = c.memberId 
			WHERE c.noteId = :noteId
		""")
    List<ChatResponse> findAllByNoteId(@Param("noteId") Long noteId);
}
