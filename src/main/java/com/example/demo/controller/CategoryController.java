package com.example.demo.controller;

import com.example.demo.modal.Category;
import com.example.demo.modal.ParentCategory;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.exception.ResourceNotFoundException; // Import the exception

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	@Value("${upload.path}")
	private String pathUploadImage;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CategoryRepository categoryRepository;

	// API to add a new parent category
	@PostMapping("/add-subcategory")
	public ResponseEntity<Category> addSubCategory(@RequestPart("category") String categoryJson, // category as JSON
																									// string
			@RequestPart("image") MultipartFile image) throws IOException {

		// Convert the category JSON string back to a Category object
		ObjectMapper objectMapper = new ObjectMapper();
		Category category = objectMapper.readValue(categoryJson, Category.class);

		if (category.getParentCategory() == null) {
			return ResponseEntity.badRequest().build(); // Subcategory requires a parent category
		}

		Category savedCategory = categoryService.addCategory(category, image);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
	}

	@GetMapping("/parent/{parentId}")
	public List<CategoryDTO> getCategoriesByParentId(@PathVariable Long parentId) {
		return categoryService.getCategoriesByParentId(parentId);
	}

	@PutMapping("/delete/{id}")
	public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
		// Fetch category by ID
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

		// Set the 'deleted' flag to true for soft delete
		category.setDeleted(true);

		// Save the category with the 'deleted' flag set to true
		try {
			categoryRepository.save(category);
		} catch (Exception e) {
			// Log error
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "Error deleting category"));
		}

		// Return success response
		return ResponseEntity.ok(Map.of("status", "success", "message", "Category deleted successfully."));
	}

	@GetMapping("/{subCategoryId}")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long subCategoryId) {
		// Fetch the Category entity by subCategoryId
		Category category = categoryRepository.findById(subCategoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + subCategoryId));

		// Map Category to CategoryDTO
		CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName(),
				category.getParentCategory() != null ? category.getParentCategory().getId() : null);

		// Return the DTO as the response
		return ResponseEntity.ok(categoryDTO);
	}

	@PutMapping("/{subCategoryId}")
	public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long subCategoryId,
			@RequestParam("category") String subCategoryDataJson,
			@RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

		// Fetch the existing category by ID
		Category category = categoryRepository.findById(subCategoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + subCategoryId));

		// Convert the category JSON string into CategoryDTO to update the fields
		ObjectMapper objectMapper = new ObjectMapper();
		CategoryDTO subCategoryData = objectMapper.readValue(subCategoryDataJson, CategoryDTO.class);

		// Check if a category with the same name already exists under the same parent
		// Since we don't need to update parentCategory, we skip the logic for
		// parentCategory
		Category existingCategory = categoryRepository.findByName(subCategoryData.getName());
		if (existingCategory != null && !existingCategory.getId().equals(subCategoryId)) {
			// If a category with the same name exists, throw an exception
			throw new IllegalArgumentException(
					"Category with name '" + subCategoryData.getName() + "' already exists.");
		}

		// Update the fields of the category entity with data from the DTO
		category.setName(subCategoryData.getName());

		// Handle image upload (if present)
		if (image != null && !image.isEmpty()) {
			// Save the new image and update the category entity with the new image URL or
			// path
			String imageUrl = saveImage(image); // Implement the saveImage method
			category.setImageUrl(imageUrl); // Update the image URL with the new image
		}

		// Save the updated category
		Category updatedCategory = categoryRepository.save(category);

		// Map the updated category to CategoryDTO and return the response
		CategoryDTO categoryDTO = new CategoryDTO(updatedCategory.getId(), updatedCategory.getName(),
				updatedCategory.getImageUrl());

		return ResponseEntity.ok(categoryDTO);
	}

	private String saveImage(MultipartFile image) throws IOException {
		// Ensure the upload directory exists or create it
		File uploadDir = new File(pathUploadImage);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs(); // Create the directory if it doesn't exist
		}

		// Generate a unique filename for the image based on timestamp and original
		// filename
		String originalFileName = image.getOriginalFilename();
		String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf("."))
				: "";
		String imageFileName = System.currentTimeMillis() + extension; // Only append the extension

		// Create the full path for saving the file
		File imageFile = new File(uploadDir, imageFileName);

		// Save the image to the disk
		try (FileOutputStream fos = new FileOutputStream(imageFile)) {
			fos.write(image.getBytes());
		}

		// Return the relative URL or filename to access the image
		return imageFileName; // Return only the file name, no path
	}
}
