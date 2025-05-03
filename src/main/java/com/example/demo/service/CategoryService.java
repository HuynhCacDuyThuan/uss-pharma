package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.ParentCategory;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.repository.ProductRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Value("${upload.path}")
    private String pathUploadImage;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
  @Autowired
  private ProductRepository productRepository ;
    // Add a method to check for duplicates before adding a category
    public Category addCategory(Category category, MultipartFile image) throws IOException {
        // Check if a category with the same name already exists
        Category existingCategory = categoryRepository.findByNameAndParentCategory(category.getName(), category.getParentCategory());

        if (existingCategory != null) {
            // If a category with the same name exists, throw an exception
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists under the same parent.");
        }

        // Ensure the upload directory exists or create it
        File uploadDir = new File(pathUploadImage);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();  // Create the directory if it doesn't exist
        }

        // Handle the image upload
        if (!image.isEmpty()) {
            String imageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();  // Ensure unique filename
            File imageFile = new File(uploadDir, imageFileName);
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(image.getBytes());
            }
            category.setImageUrl(imageFileName);  // Set the image URL in the category object
        }

        // If no duplicate exists, proceed with saving the category
        return categoryRepository.save(category);
    }
    public List<CategoryDTO> getCategoriesByParentId(Long parentId) {
        // Find categories by parentId and filter by deleted status
        List<Category> categories = categoryRepository.findByParentCategoryIdAndDeletedFalse(parentId);
        
        List<CategoryDTO> categoryDTOs = new ArrayList<>();

        for (Category category : categories) {
            categoryDTOs.add(new CategoryDTO(
                category.getId(), 
                category.getName(), 
                category.getParentCategory() != null ? category.getParentCategory().getId() : null
            ));
        }

        return categoryDTOs;
    }
    public int getProductCountByCategoryId(Long categoryId) {
        // Query the database to count the number of products in the given category
        return productRepository.countByCategoryId(categoryId);
    }
}
