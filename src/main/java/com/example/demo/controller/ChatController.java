package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatMessage;
import com.example.demo.modal.Message;
import com.example.demo.modal.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;

@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        User sender = userRepository.findByUsername(chatMessage.getSenderUsername()).orElseThrow();
        User receiver = userRepository.findByUsername(chatMessage.getReceiverUsername()).orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        
        // Gửi tin nhắn tới người nhận
        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getReceiverUsername(), chatMessage);

        // Gửi lại tin nhắn cho người gửi (để đồng bộ nhiều tab trình duyệt nếu có)
        messagingTemplate.convertAndSend("/topic/messages/" + chatMessage.getSenderUsername(), chatMessage);

    }
    
}
