package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kênh trả dữ liệu về client
        config.enableSimpleBroker("/topic");

        
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint kết nối WebSocket, sử dụng SockJS để fallback khi không hỗ trợ WebSocket
        registry.addEndpoint("/ws") // 👈 client kết nối tại: new SockJS("/ws")
                .setAllowedOriginPatterns("*") // Cho phép CORS tất cả origin
                .withSockJS(); // Bắt buộc nếu phía frontend dùng SockJS
    }
}
