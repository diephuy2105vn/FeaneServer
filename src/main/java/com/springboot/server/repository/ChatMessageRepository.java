package com.springboot.server.repository;

import com.springboot.server.models.ChatMessage;
import com.springboot.server.models.ChatRoom;
import com.springboot.server.models.User;
import jakarta.persistence.OrderBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findByIdAndSender(Long id, User sender);
    Set<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);

}
