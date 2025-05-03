package com.example.demo.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private String timestamp;  // định dạng String ISO hoặc giờ địa phương
}
