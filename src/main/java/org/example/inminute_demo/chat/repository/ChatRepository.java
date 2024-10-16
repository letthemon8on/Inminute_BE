package org.example.inminute_demo.chat.repository;

import org.example.inminute_demo.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {


}
