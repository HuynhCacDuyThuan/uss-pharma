package com.example.demo.modal;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT") // ✅ Chuyển thành LONGTEXT
    private String content;

    @Column(length = 500)
    private String mainImageUrl;

    @Column(length = 255)  // Thêm cột imageTitle
    private String imageTitle;  // Tiêu đề ảnh

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Ngày tạo

    @Column(nullable = false)
    private String createdBy; // Người tạo

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "news_id")
    private List<Subtitle> subtitles;
    @Transient
    private String formattedCreatedAt;
    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }

    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
