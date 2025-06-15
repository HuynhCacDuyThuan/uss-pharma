package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import com.example.demo.modal.Logo;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.LogoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private LogoService logoService;

	@GetMapping("/dang-nhap")
	public String loginPage(@RequestParam(value = "error", required = false) String error, Model model,
			HttpServletRequest request) {
		if (error != null) {
			model.addAttribute("errorMessage", "Thông tin đăng nhập không đúng.");
		}
		List<Logo> logos = logoService.getAllLogos();
		String baseUrl = getBaseUrl(request);

		String logoUrl = null;

		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

		}

		model.addAttribute("logoUrl", logoUrl);
		List<Map<String, Object>> graph = new ArrayList<>();

		Map<String, Object> logo = Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url", baseUrl + logoUrl,
				"contentUrl", logoUrl, "caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi", "width",
				"501", "height", "323");

		// Địa chỉ
		Map<String, Object> address = Map.of("@type", "PostalAddress", "streetAddress", "157 Đống Đa, phường Thị Nại",
				"addressLocality", "TP. Quy Nhơn", "addressRegion", "Bình Định", "postalCode", "55000",
				"addressCountry", "VN");

		// Organization
		Map<String, Object> organization = Map.of("@type", "Organization", "@id", baseUrl + "/#organization", "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "url", baseUrl, "logo", logo, "address", address,
				"openingHoursSpecification",
				List.of(Map.of("@type", "OpeningHoursSpecification", "dayOfWeek",
						List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), "opens",
						"09:00", "closes", "17:00")));

		// WebSite
		Map<String, Object> website = Map.of("@type", "WebSite", "@id", baseUrl + "/#website", "url", baseUrl, "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "alternateName", "USS PHARMA", "publisher",
				Map.of("@id", baseUrl + "/#organization"), "inLanguage", "vi");

		// BreadcrumbList cho trang đăng nhập
		Map<String, Object> breadcrumbList = Map.of("@type", "BreadcrumbList", "@id",
				baseUrl + "/dang-nhap/#breadcrumb", "itemListElement",
				List.of(Map.of("@type", "ListItem", "position", 1, "item", Map.of("@id", baseUrl, "name", "Trang chủ")),
						Map.of("@type", "ListItem", "position", 2, "item",
								Map.of("@id", baseUrl + "/dang-nhap", "name", "Đăng nhập"))));

		// WebPage đại diện cho trang đăng nhập
		Map<String, Object> webPage = Map.of("@type", "WebPage", "@id", baseUrl + "/dang-nhap/#webpage", "url",
				baseUrl + "/dang-nhap", "name", "Đăng nhập - Công ty TNHH Dược Phẩm Quốc tế USS", "isPartOf",
				Map.of("@id", baseUrl + "/#website"), "inLanguage", "vi", "breadcrumb",
				Map.of("@id", baseUrl + "/dang-nhap/#breadcrumb"), "about",
				Map.of("@type", "Thing", "name", "Trang đăng nhập người dùng"));

		// LoginAction mô tả hành động đăng nhập
		Map<String, Object> loginAction = Map.of("@type", "LoginAction", "target", baseUrl + "/dang-nhap", "name",
				"Đăng nhập người dùng");

		// Thêm tất cả vào graph
		graph.add(organization);
		graph.add(website);
		graph.add(breadcrumbList);
		graph.add(webPage);
		graph.add(loginAction); // Đã thêm LoginAction vào graph

		Map<String, Object> jsonLd = Map.of("@context", "https://schema.org", "@graph", graph);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String schemaJson = objectMapper.writeValueAsString(jsonLd);
			model.addAttribute("schemaJson", schemaJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			model.addAttribute("schemaJson", "{}");
		}
		return "auth-login"; // Redirect to the login page
	}

	private String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getHeader("X-Forwarded-Proto");
		if (scheme == null || scheme.isEmpty()) {
			scheme = request.getScheme();
		}

		String host = request.getHeader("X-Forwarded-Host");
		if (host == null || host.isEmpty()) {
			host = request.getServerName();
		}

		String port = request.getHeader("X-Forwarded-Port");
		if (port == null || port.isEmpty()) {
			port = String.valueOf(request.getServerPort());
		}

		String baseUrl = scheme + "://" + host;

//			// Nếu port không phải mặc định thì thêm vào URL
//			if ((scheme.equals("http") && !"80".equals(port)) || (scheme.equals("https") && !"443".equals(port))) {
//				baseUrl += ":" + port;
//			}

		return baseUrl;
	}

	@PostMapping("/dang-nhap")
	public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
			HttpServletResponse response, HttpSession session) {
		try {
			// Attempt login with email and password
			System.out.println("Attempting to login with email: " + username);

			// Create Authentication object for email and password
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
					password);

			// Authenticate user
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Successful login
			System.out.println("Authentication successful for: " + authentication.getName());

			// Set user details in session
			session.setAttribute("username", authentication.getName());

			// Get authorities and determine redirection
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			String role = authorities.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("ROLE_USER");
			System.out.println("Authorities: " + authorities);

			// Redirect based on role
			if ("ROLE_ADMIN".equals(role)) {
				return "redirect:/quan-tri"; // Redirect to Admin page
			} else {
				return "redirect:/user/index"; // Redirect to User page
			}

		} catch (Exception e) {
			// Log error and return to login page with error message
			System.out.println("Authentication failed: " + e.getMessage());
			return "auth-login"; // Return to login page
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
