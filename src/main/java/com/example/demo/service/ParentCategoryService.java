package com.example.demo.service;

import com.example.demo.modal.ParentCategory;
import com.example.demo.repository.ParentCategoryRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ParentCategoryService {

    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
    public Page<ParentCategory> getAllParentCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Create a Pageable object with the given page and size
        return parentCategoryRepository.findAll(pageable);  // Return a Page of ParentCategory
    }
    // Phương thức lưu ParentCategory vào cơ sở dữ liệu
    public ParentCategory saveParentCategory(ParentCategory parentCategory) {
        return parentCategoryRepository.save(parentCategory); // Lưu và trả về đối tượng đã được lưu
    }
}
