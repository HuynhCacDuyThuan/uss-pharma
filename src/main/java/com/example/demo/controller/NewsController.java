package com.example.demo.controller;

import com.example.demo.modal.ApiResponse;
import com.example.demo.modal.News;
import com.example.demo.modal.Subtitle;
import com.example.demo.request.PostRequest;
import com.example.demo.request.SubtitleRequest;
import com.example.demo.service.NewsService;
import com.example.demo.repository.NewsRepository;
import com.example.demo.repository.SubtitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Value("${upload.path}")
    private String pathUploadImage;

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private NewsService newsService;
    @Autowired
    private SubtitleRepository subtitleRepository;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addNews(@RequestBody PostRequest postRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Tạo đối tượng News mới
            News news = new News();
            news.setTitle(postRequest.getTitle());
            news.setContent(postRequest.getContent());

            // Xử lý ảnh chính
            if (postRequest.getMainImage() != null && !postRequest.getMainImage().isEmpty()) {
                String mainImageFileName = saveBase64Image(postRequest.getMainImage(), new File(pathUploadImage));
                news.setMainImageUrl(mainImageFileName);
                news.setImageTitle(postRequest.getImageTitle()); // Lưu tiêu đề ảnh chính
            } else {
                news.setMainImageUrl(null);
                news.setImageTitle(null);
            }

            news.setCreatedBy("admin");

            // Xử lý danh sách Subtitle
            List<Subtitle> subtitles = new ArrayList<>();
            if (postRequest.getSubtitles() != null) {
                for (SubtitleRequest subReq : postRequest.getSubtitles()) {
                    Subtitle subtitle = new Subtitle();
                    subtitle.setSubtitle(subReq.getSubtitle());
                    subtitle.setImageTitle(subReq.getImageTitle()); // Lưu tiêu đề ảnh phụ

                    if (subReq.getImage() != null && !subReq.getImage().isEmpty()) {
                        String subtitleImageFileName = saveBase64Image(subReq.getImage(), new File(pathUploadImage));
                        subtitle.setImageUrl(subtitleImageFileName);
                    } else {
                        subtitle.setImageUrl(null);
                    }

                    subtitles.add(subtitle);
                }
            }

            news.setSubtitles(subtitles);

            // Lưu vào database
            News savedNews = newsRepository.save(news);

            // Phản hồi JSON
            response.put("status", "success");
            response.put("message", "Bài viết đã được thêm thành công!");
            response.put("data", Map.of(
                    "id", savedNews.getId(),
                    "title", savedNews.getTitle(),
                    "mainImageUrl", savedNews.getMainImageUrl(),
                    "subtitles", savedNews.getSubtitles()
            ));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Có lỗi xảy ra khi thêm bài viết: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateNews(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find the existing news article by ID
            News existingNews = newsRepository.findById(id).orElse(null);
            if (existingNews == null) {
                response.put("status", "error");
                response.put("message", "Bài viết không tồn tại!");
                return ResponseEntity.status(404).body(response);
            }

            // Update the news properties
            existingNews.setTitle(postRequest.getTitle());
            existingNews.setContent(postRequest.getContent());

            // Process the main image (if provided)
            if (postRequest.getMainImage() != null && !postRequest.getMainImage().isEmpty()) {
                String mainImageFileName = saveBase64Image(postRequest.getMainImage(), new File(pathUploadImage));
                existingNews.setMainImageUrl(mainImageFileName);
                existingNews.setImageTitle(postRequest.getImageTitle()); // Save the main image title
            }

            // Handle subtitles (if provided)
            List<Subtitle> updatedSubtitles = new ArrayList<>();
            if (postRequest.getSubtitles() != null) {
                // Remove subtitles that are no longer referenced
                for (SubtitleRequest subReq : postRequest.getSubtitles()) {
                    Subtitle subtitle = new Subtitle();
                    subtitle.setSubtitle(subReq.getSubtitle());
                    subtitle.setImageTitle(subReq.getImageTitle()); // Save the subtitle image title

                    if (subReq.getImage() != null && !subReq.getImage().isEmpty()) {
                        String subtitleImageFileName = saveBase64Image(subReq.getImage(), new File(pathUploadImage));
                        subtitle.setImageUrl(subtitleImageFileName);
                    }

                    updatedSubtitles.add(subtitle);
                }
            }

            // Ensure only existing subtitles that are referenced are kept
            existingNews.getSubtitles().clear();  // Clear existing references
            existingNews.getSubtitles().addAll(updatedSubtitles); // Add updated list of subtitles

            // Save the updated news in the database
            News updatedNews = newsRepository.save(existingNews);

            // Respond with a success message
            response.put("status", "success");
            response.put("message", "Bài viết đã được cập nhật thành công!");
            response.put("data", Map.of(
                    "id", updatedNews.getId(),
                    "title", updatedNews.getTitle(),
                    "mainImageUrl", updatedNews.getMainImageUrl(),
                    "subtitles", updatedNews.getSubtitles()
            ));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Có lỗi xảy ra khi cập nhật bài viết: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Hàm hỗ trợ lưu ảnh từ base64
    private String saveBase64Image(String base64Data, File uploadDir) throws IOException {
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        String fileName = System.currentTimeMillis() + ".png"; // Tên file ảnh

        File imageFile = new File(uploadDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageBytes);
        }
        return fileName; // Trả về tên file đã lưu
    }
}
