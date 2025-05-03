package com.example.demo.controller;



import com.example.demo.dto.RegisterDTO;
import com.example.demo.modal.ErrorResponse;
import com.example.demo.modal.LoginRequest;
import com.example.demo.modal.Role;
import com.example.demo.modal.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private  CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authenticationManager;
    public AuthController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }@PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {
        // Check if phone number already exists
        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("{\"message\": \"SỐ ĐIỆN THOẠI NÀY ĐÃ TỒN TẠI!\"}");
        }
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("{\"message\": \"EMAIL NÀY ĐÃ TỒN TẠI!\"}");
        }
        // Create a new User object
        User newUser = new User();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setEmail(registerDTO.getEmail());
     

        // Encrypt the password using BCryptPasswordEncoder
        String encodedPassword = new BCryptPasswordEncoder().encode(registerDTO.getPassword());
        newUser.setPassword(encodedPassword);

        // Assign the default role to the new user
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roles.add(userRole);
        newUser.setRoles(roles);

        // Save the user to the repository
        userRepository.save(newUser);

        return ResponseEntity.ok("{\"message\": \"ĐĂNG KÍ TÀI KHOẢN THÀNH CÔNG!\"}");
    }

   

}
