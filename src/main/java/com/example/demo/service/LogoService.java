package com.example.demo.service;

import com.example.demo.modal.Logo;
import com.example.demo.repository.LogoRepository;
import com.example.demo.config.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class LogoService {

	private final LogoRepository logoRepository;
	private final FileStorageProperties fileStorageProperties;

	public LogoService(LogoRepository logoRepository, FileStorageProperties fileStorageProperties) {
		this.logoRepository = logoRepository;
		this.fileStorageProperties = fileStorageProperties;
	}

	public Logo saveLogo(MultipartFile file) throws IOException {
		String uploadDir = fileStorageProperties.getUploadPath();
		File dir = new File(uploadDir);
		if (!dir.exists())
			dir.mkdirs();

		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		File destination = new File(dir, fileName);

		try (FileOutputStream fos = new FileOutputStream(destination)) {
			fos.write(file.getBytes());
		}

		Logo logo = new Logo();
		logo.setImageUrl(fileName);
		return logoRepository.save(logo);
	}

	public List<Logo> getAllLogos() {
		return logoRepository.findAll();
	}

	public Logo updateLogo(Long id, MultipartFile file) throws IOException {
		Logo logo = logoRepository.findById(id).orElseThrow(() -> new RuntimeException("Logo không tồn tại"));

		// Xóa file cũ nếu muốn (nếu có lưu đường dẫn đầy đủ)
		String uploadDir = fileStorageProperties.getUploadPath();
		File oldFile = new File(uploadDir, logo.getImageUrl());
		if (oldFile.exists())
			oldFile.delete();

		// Lưu file mới
		String newFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		File newFile = new File(uploadDir, newFileName);
		try (FileOutputStream fos = new FileOutputStream(newFile)) {
			fos.write(file.getBytes());
		}

		logo.setImageUrl(newFileName);
		return logoRepository.save(logo);
	}

	public void deleteLogo(Long id) {
		Logo logo = logoRepository.findById(id).orElseThrow(() -> new RuntimeException("Logo không tồn tại"));

		String uploadDir = fileStorageProperties.getUploadPath();
		File file = new File(uploadDir, logo.getImageUrl());
		if (file.exists())
			file.delete();

		logoRepository.deleteById(id);
	}
}
