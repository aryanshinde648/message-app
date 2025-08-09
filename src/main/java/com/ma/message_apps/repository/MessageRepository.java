package com.ma.message_apps.repository;

import com.ma.message_apps.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {
    @Query("SELECT m FROM Message m WHERE (m.sender.userId = :fromUserId AND m.receiver.userId = :toUserId) OR (m.sender.userId = :toUserId AND m.receiver.userId = :fromUserId) ORDER BY m.createdAt ASC")
    List<Message> findChatMessages(@org.springframework.data.repository.query.Param("fromUserId") Integer fromUserId, @org.springframework.data.repository.query.Param("toUserId") Integer toUserId);
}
