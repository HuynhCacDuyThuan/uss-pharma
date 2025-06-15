package com.example.demo.controller;

import com.example.demo.modal.Logo;
import com.example.demo.service.LogoService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/logo")
public class LogoController {

    private final LogoService logoService;

    public LogoController(LogoService logoService) {
        this.logoService = logoService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<Logo>> getAllLogos() {
        try {
            List<Logo> logos = logoService.getAllLogos();
            return ResponseEntity.ok(logos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<Logo> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            Logo logo = logoService.saveLogo(file);
            return ResponseEntity.ok(logo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // API sửa logo (thay file mới)
    @PutMapping("/update/{id}")
    public ResponseEntity<Logo> updateLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Logo updatedLogo = logoService.updateLogo(id, file);
            return ResponseEntity.ok(updatedLogo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // API xóa logo
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLogo(@PathVariable Long id) {
        try {
            logoService.deleteLogo(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
