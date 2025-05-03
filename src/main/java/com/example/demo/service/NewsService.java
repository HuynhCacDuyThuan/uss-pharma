package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.modal.News;
import com.example.demo.repository.NewsRepository;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    // Method to get all News
    public List<News> getAllNews1() {
        return newsRepository.findAll();
    }
    public Page<News> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }
    public News getNewsByTitle(String title) {
        return newsRepository.findByTitle(title);  // Assuming 'title' is a field in your News model
    }
    public News getNewsById(Long id) {
        Optional<News> news = newsRepository.findById(id);
        return news.orElse(null);
    }
    
    public List<News> getTop5News() {
        return newsRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
