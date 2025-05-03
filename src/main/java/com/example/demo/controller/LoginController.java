package com.example.demo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.modal.LoginForm;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	@Autowired
	 private UserRepository userRepository;
	@Autowired
	    private  RoleRepository roleRepository;
	    @Autowired
	    private  CustomUserDetailsService customUserDetailsService;
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    private  AuthenticationManager authenticationManager;
	    @GetMapping("/dang-nhap")
	    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
	        if (error != null) {
	            model.addAttribute("errorMessage", "Thông tin đăng nhập không đúng.");
	        }
	        return "auth-login"; // Redirect to the login page
	    }
	    @PostMapping("/dang-nhap")
	    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
	                            HttpServletResponse response, HttpSession session) {
	        try {
	            // Attempt login with email and password
	            System.out.println("Attempting to login with email: " + username);

	            // Create Authentication object for email and password
	            UsernamePasswordAuthenticationToken authenticationToken = 
	                    new UsernamePasswordAuthenticationToken(username, password);

	            // Authenticate user
	            Authentication authentication = authenticationManager.authenticate(authenticationToken);
	            SecurityContextHolder.getContext().setAuthentication(authentication);

	            // Successful login
	            System.out.println("Authentication successful for: " + authentication.getName());

	            // Set user details in session
	            session.setAttribute("username", authentication.getName());

	            // Get authorities and determine redirection
	            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	            String role = authorities.stream()
	                    .map(GrantedAuthority::getAuthority)
	                    .findFirst()
	                    .orElse("ROLE_USER");
	            System.out.println("Authorities: " + authorities);

	            // Redirect based on role
	            if ("ROLE_ADMIN".equals(role)) {
	                return "redirect:/quan-tri";  // Redirect to Admin page
	            } else {
	                return "redirect:/user/index";  // Redirect to User page
	            }

	        } catch (Exception e) {
	            // Log error and return to login page with error message
	            System.out.println("Authentication failed: " + e.getMessage());
	            return "auth-login";  // Return to login page
	        }
	    }
	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        // Xóa session email khi người dùng đăng xuất
	        session.invalidate(); 

	        // Xóa thông tin xác thực khỏi SecurityContext
	        SecurityContextHolder.clearContext();

	        // Chuyển hướng về trang login sau khi đăng xuất
	        return "redirect:/";
	    }

}
