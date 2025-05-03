package com.example.demo.controller;

import com.example.demo.dto.CommentDTO;
import com.example.demo.modal.Comment;
import com.example.demo.modal.News;
import com.example.demo.modal.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.NewsRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsRepository newsRepository;

    @PostMapping
    public ResponseEntity<?> addComment(
            @RequestParam Long newsId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập để bình luận");
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("Người dùng không tồn tại");

        News news = newsRepository.findById(newsId).orElse(null);
        if (news == null) return ResponseEntity.badRequest().body("Bài viết không tồn tại");

        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nội dung bình luận không được để trống");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setNews(news);
        comment.setContent(content);
        comment.setTimestamp(LocalDateTime.now());
        commentRepository.save(comment);

        return ResponseEntity.ok("Bình luận đã được lưu");
    }


    // ✅ Lấy bình luận theo bài viết
    @GetMapping("/by-news")
    public List<CommentDTO> getCommentsByNews(@RequestParam Long newsId) {
        List<Comment> comments = commentRepository.findByNewsIdOrderByTimestampDesc(newsId);

        return comments.stream().map(c -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(c.getId());
            dto.setContent(c.getContent());
            dto.setUsername(c.getUser().getUsername());
            dto.setTimestamp(c.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}
