package com.example.demo.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String email;
  
    private String password; // Thêm dòng này
    private String username; // Thêm username vào DTO
}
