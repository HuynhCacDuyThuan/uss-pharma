package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.modal.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
  
}
