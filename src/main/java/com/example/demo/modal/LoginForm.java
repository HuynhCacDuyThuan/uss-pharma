package com.example.demo.modal;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    private String email;  // Dùng email thay vì username
    private String password;
}