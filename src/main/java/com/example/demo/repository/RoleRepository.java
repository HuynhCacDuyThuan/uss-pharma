package com.example.demo.repository;



import com.example.demo.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Tìm Role theo tên
    Optional<Role> findByName(String name);
}
