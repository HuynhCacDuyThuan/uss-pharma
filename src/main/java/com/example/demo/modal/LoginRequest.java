package com.example.demo.modal;

public class LoginRequest {

    private String name;         // Tên người dùng
    private String phoneNumber;  // Số điện thoại

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
