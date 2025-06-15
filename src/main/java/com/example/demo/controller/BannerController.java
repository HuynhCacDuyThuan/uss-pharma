package com.example.demo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.modal.Banner;
import com.example.demo.repository.BannerRepository;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Value("${upload.path}")
    private String pathUploadImage;

    @Autowired
    private BannerRepository bannerRepository;

    @GetMapping("/all")
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    // Thêm banner (upload file)
    @PostMapping("/add")
    public ResponseEntity<?> addBanner(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required");
        }
        try {
            File uploadDir = new File(pathUploadImage);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File savedFile = new File(uploadDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                fos.write(file.getBytes());
            }
            Banner banner = new Banner();
            banner.setImageUrl(fileName);
            bannerRepository.save(banner);
            return ResponseEntity.ok(banner);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    // Xoá banner
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBanner(@PathVariable Long id) {
        if (!bannerRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Banner not found");
        }
        bannerRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

  
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateBanner(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Banner not found");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required");
        }
        try {
            File uploadDir = new File(pathUploadImage);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File savedFile = new File(uploadDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(savedFile)) {
                fos.write(file.getBytes());
            }
            Banner banner = optionalBanner.get();
            banner.setImageUrl(fileName);
            bannerRepository.save(banner);
            return ResponseEntity.ok(banner);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }
}