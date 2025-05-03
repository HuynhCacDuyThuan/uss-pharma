package com.example.demo.repository;

import com.example.demo.modal.ParentCategory;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
@Repository
public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Long> {
	  Page<ParentCategory> findAll(Pageable pageable); // This method will return a Page of ParentCategory with pagination support
}