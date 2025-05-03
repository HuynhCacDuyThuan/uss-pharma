package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.modal.Message;
import com.example.demo.modal.User;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.username = :username1 AND m.receiver.username = :username2) " +
            "   OR (m.sender.username = :username2 AND m.receiver.username = :username1) " +
            "ORDER BY m.timestamp ASC")
     List<Message> findChatBetweenUsers(String username1, String username2);
    List<Message> findAll();
}