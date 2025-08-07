package com.hkust.smart_buddy.chatroom.repository;

import com.hkust.smart_buddy.chatroom.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
    Optional<Message> findByMessageId(String messageId);
}
