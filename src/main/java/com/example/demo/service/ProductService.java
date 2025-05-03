package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.ParentCategory;
import com.example.demo.modal.Product;
import com.example.demo.modal.ProductImage;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    // Use the relative directory path consistent with the ExportedFileController
    private static final String UPLOAD_DIR = "uploads/img";  // Ensure the directory path is consistent

    // Define allowed image file types and size limit (in bytes)
    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/bmp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ParentCategoryRepository parentCategoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;


    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByDeletedFalse(pageable); // Fetch only products that are not deleted
    }
}
