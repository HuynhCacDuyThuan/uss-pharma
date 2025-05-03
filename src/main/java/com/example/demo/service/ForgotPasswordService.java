package com.example.demo.service;

import com.example.demo.modal.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean processForgotPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false; // Email không tồn tại
        }

        User user = optionalUser.get();

        // Tạo mật khẩu mới
        String newPassword = generateRandomPassword(8);

        // Cập nhật vào DB với mật khẩu mã hóa
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Gửi email
        sendEmail(email, newPassword);
        return true;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("USS PHARMA - Mật khẩu mới");
        message.setText("Chào bạn,\n\nMật khẩu mới của bạn là: " + newPassword + "\n\nTrân trọng,\nUSS PHARMA");
        mailSender.send(message);
    }
}
