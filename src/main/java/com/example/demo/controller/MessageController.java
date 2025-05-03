package com.example.demo.controller;

import com.example.demo.modal.Message;
import com.example.demo.repository.MessageRepository;
import com.example.demo.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    MessageService messageService;
    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }
    @GetMapping("/between")
    public List<Message> getMessagesBetweenUsers(@RequestParam String user1, @RequestParam String user2) {
        return messageRepository.findChatBetweenUsers(user1, user2);
    }
}
