package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class RegisterController {
	 @GetMapping("/dang-ki")
	 public String getRegister() {
		 return "auth-register";
	 }
}
