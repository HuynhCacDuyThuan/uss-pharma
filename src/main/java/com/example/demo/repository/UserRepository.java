package com.example.demo.repository;


import com.example.demo.modal.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
   
    Optional<User> findByEmail(String email);  // Tìm người dùng theo email
    @Query("SELECT u FROM User u")
    Page<User> findAllUsers(Pageable pageable);
}
