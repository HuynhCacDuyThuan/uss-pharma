package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import com.example.demo.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	 @Autowired
	    private UserDetailsService userDetailsService;
 

    
    

	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http.csrf().disable()
	            .authorizeRequests(authorizeRequests -> 
	                authorizeRequests
	                    .requestMatchers("/api/comments/**","/quen-mat-khau", "/chi-tiet-san-pham/**","/san-pham","/chi-tiet-bai-viet/**","/tin-tuc","/quan-ly-tin-tuc/**", "/chi-tiet-tin-tuc","/api/news/**","/tin-tuc","/api/news/add","/them-bai-viet" ,"/admin/news" ,"/api/jobs", "/api/jobs/**", "/admin/job","/api/categories","/dang-nhap","/" , "/lien-he" ,"/gioi-thieu","/dang-ki","/api/products/**",  "/auth/register", "/auth/login", "/css/**", "/fragments/**", "/js/**", "/images/**" , "/js/**","/assets/**", "/fonts/**","/libs/**","/scss/**","/tasks/**", "/loadImage**","/product-details" ,"/tuyen-dung" , "/api/submit-contact" , "/details/**","/api/categories/add-subcategory" ,"/product/**")
	                    .permitAll()
	                    .requestMatchers("/favicon.ico").permitAll()
	                    .requestMatchers("/admin/**").hasRole("ADMIN")
	                    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
	                    .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	            	    .loginPage("/dang-nhap")
	            	    .defaultSuccessUrl("/", true)
	            	    .permitAll()
	            	
	            )
	            .logout(logout -> logout
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/")
	                .permitAll()
	            )
	            .exceptionHandling(configurer -> configurer
	                .accessDeniedPage("/login") // Chuyển hướng về trang login nếu không có quyền truy cập
	            );
	        return http.build();
	    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService) // Your custom user details service
            .passwordEncoder(passwordEncoder()); // Your password encoder
        return authenticationManagerBuilder.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
}
