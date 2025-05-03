package com.example.demo.request;

public class SubtitleRequest {
    private Long id;
    private String subtitle;
    private String image; // Base64 string
    private String imageTitle; // Tiêu đề ảnh phụ

    // Constructor
    public SubtitleRequest() {
    }

    // Getters and Setters
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    @Override
    public String toString() {
        return "SubtitleRequest [id=" + id + ", subtitle=" + subtitle + ", image=" + image + ", imageTitle=" + imageTitle + "]";
    }
}
