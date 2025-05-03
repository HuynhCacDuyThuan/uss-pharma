package com.example.demo.modal;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người bình luận
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Bài viết được bình luận
    @ManyToOne
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;
}
