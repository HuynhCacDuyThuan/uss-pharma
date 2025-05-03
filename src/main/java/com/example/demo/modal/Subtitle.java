package com.example.demo.modal;

import jakarta.persistence.*;

@Entity
@Table(name = "subtitles")
public class Subtitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String subtitle;

    private String imageUrl;

    // Thêm cột để lưu tiêu đề ảnh phụ
    private String imageTitle;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter và setter cho imageTitle
    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    @Override
    public String toString() {
        return "Subtitle [id=" + id + ", subtitle=" + subtitle + ", imageUrl=" + imageUrl + ", imageTitle=" + imageTitle + "]";
    }
}
