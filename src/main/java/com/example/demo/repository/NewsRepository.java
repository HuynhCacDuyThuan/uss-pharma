package com.example.demo.repository;

import com.example.demo.modal.News;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
	News findByTitle(String title);
	   List<News> findTop5ByOrderByCreatedAtDesc();
	   
}
