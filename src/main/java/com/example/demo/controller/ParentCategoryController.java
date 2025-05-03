package com.example.demo.controller;


import com.example.demo.dto.ParentCategoryDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.modal.ParentCategory;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.service.ParentCategoryService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parent-categories")
public class ParentCategoryController {
	 @Autowired
	 private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private ParentCategoryService parentCategoryService;

    // API thêm một ParentCategory
    @PostMapping
    public ResponseEntity<ParentCategory> addParentCategory(@RequestBody ParentCategory parentCategory) {
        ParentCategory createdParentCategory = parentCategoryService.saveParentCategory(parentCategory);
        return ResponseEntity.status(201).body(createdParentCategory); // Trả về ParentCategory vừa được thêm vào
    }
    @PutMapping("/{id}")
    public ResponseEntity<ParentCategory> updateCategory(@PathVariable Long id, @RequestBody ParentCategory updatedCategory) {
        ParentCategory existingCategory = parentCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        existingCategory.setName(updatedCategory.getName());
        ParentCategory savedCategory = parentCategoryRepository.save(existingCategory);

        return ResponseEntity.ok(savedCategory);
    }
    
    @GetMapping
    public List<ParentCategoryDTO> getAllParentCategories() {
        List<ParentCategory> parentCategories = parentCategoryRepository.findAll();

        // Chuyển đổi từ ParentCategory sang ParentCategoryDTO
        return parentCategories.stream()
                .map(category -> new ParentCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}
