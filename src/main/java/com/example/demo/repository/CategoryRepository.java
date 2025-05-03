package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modal.Category;
import com.example.demo.modal.ParentCategory;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	  // Find Category by name and ParentCategory
    Category findByNameAndParentCategory(String name, ParentCategory parentCategory);
    List<Category> findByParentCategoryId(Long parentId);
    List<Category> findByParentCategoryIdAndDeletedFalse(Long parentId);
    Category findByName(String name);
  
}