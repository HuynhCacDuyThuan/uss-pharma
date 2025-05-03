package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.JobRecruitment;
import com.example.demo.modal.Message;
import com.example.demo.modal.News;
import com.example.demo.modal.Product;
import com.example.demo.modal.User;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ForgotPasswordService;
import com.example.demo.service.JobRecruitmentService;
import com.example.demo.service.NewsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	NewsService newsService;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	JobRecruitmentService jobRecruitmentService; 
	 @Autowired
	 UserRepository userRepository;
	 @Autowired
	    private MessageRepository messageRepository;
	 @Autowired
	 ForgotPasswordService forgotPasswordService;
	@GetMapping("/user/index")
	public String loginPage() {
		return "user/index"; // This will map to login.html in /templates
	}
	  @PostMapping("/quen-mat-khau")
	    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
	        boolean result = forgotPasswordService.processForgotPassword(email);
	        if (result) {
	            model.addAttribute("successMessage", "Mật khẩu mới đã được gửi đến email của bạn.");
	        } else {
	            model.addAttribute("errorMessage", "Email không tồn tại trong hệ thống.");
	        }
	        return "user/forgot";
	    }
	@GetMapping("/quen-mat-khau")
	public String forgot() {
		return "user/forgot"; // This will map to login.html in /templates
	}
	@GetMapping("/tuyen-dung")
	public String job(Model model , @AuthenticationPrincipal UserDetails userDetails) {
		List<JobRecruitment> jobList = jobRecruitmentService.getAllJobs(); // Retrieve all jobs from the service
		  // Gán username nếu đã đăng nhập
	    String username = (userDetails != null) ? userDetails.getUsername() : null;
	    model.addAttribute("username", username);

	    if (username != null) {
	    	Optional<User> optionalUser = userRepository.findByUsername(username);
	    	optionalUser.ifPresent(user -> model.addAttribute("userId", user.getId()));
	        List<Message> messages = messageRepository.findChatBetweenUsers(username, "0928666622");
	        model.addAttribute("chatMessages", messages);
	    } else {
	        model.addAttribute("chatMessages", List.of()); // Gán danh sách rỗng nếu chưa đăng nhập
	    }
		model.addAttribute("jobs", jobList); // Pass the jobs list to the view
		return "user/job"; // This will map to login.html in /templates
	}
	@GetMapping("/chi-tiet-bai-viet/{title}")
	public String getNewsByTitle(Model model, @PathVariable String title) {
	    // Ensure the title is URL-decoded if necessary, in case it was encoded (e.g., %20 for spaces).
	    try {
	        // Decode title (if encoded in the URL) – this step may not be necessary if it's already handled elsewhere.
	        String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8.name());
	        
	        // Fetch news by the decoded title
	        News news = newsService.getNewsByTitle(decodedTitle); 
	        List<News>top5 =newsService.getTop5News();
	        model.addAttribute("top5News", top5);
	        if (news != null) {
	            // Format the 'createdAt' date before adding it to the model
	            news.setFormattedCreatedAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(news.getCreatedAt()));
	            model.addAttribute("news", news); // Add the fetched news to the model
	        } else {
	            // Handle the case where no news is found (optional)
	            model.addAttribute("news", new News()); // Or return a message for no data
	        }
	    } catch (UnsupportedEncodingException e) {
	        // Log the error and set a default error message if URL decoding fails
	        model.addAttribute("errorMessage", "Error decoding the news title.");
	    }
	    
	    return "user/details-news"; // Return the name of the view template
	}

	@GetMapping("/tin-tuc")
	public String getNews(Model model , @AuthenticationPrincipal UserDetails userDetails ,    @RequestParam(defaultValue = "0") int page) {

		Pageable pageable = PageRequest.of(0, 3); // Trang 0, lấy 5 sản phẩm
		Pageable pageable1 = PageRequest.of(page, 4); // Trang 0, lấy 5 sản phẩm
		// Lấy sản phẩm theo category_id và deleted = false
		Page<Product> products = productRepository.findByCategoryIdAndDeletedFalse(1L, pageable);
		List<ProductDTO> productDTOs = products.map(product -> {
			ProductDTO dto = new ProductDTO();
			dto.setId(product.getId());
			dto.setName(product.getName());
			dto.setPrice(product.getPrice());
			dto.setDescription(product.getDescription());
			dto.setSubDescription(product.getSubDescription());
			dto.setDiscount(product.getDiscount());
			dto.setCreatedDate(product.getCreatedDate());
			dto.setImageUrl(product.getImageUrl());
			dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
			dto.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
			return dto;
		}).toList(); // Chuyển về List thay vì Page
		   Page<News> pagedNews = newsService.getAllNews(pageable1);
		    pagedNews.forEach(news -> news.setFormattedCreatedAt(
		        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(news.getCreatedAt())
		    ));
		 String username = (userDetails != null) ? userDetails.getUsername() : null;
		    model.addAttribute("username", username);

		    if (username != null) {
		    	Optional<User> optionalUser = userRepository.findByUsername(username);
		    	optionalUser.ifPresent(user -> model.addAttribute("userId", user.getId()));
		        List<Message> messages = messageRepository.findChatBetweenUsers(username, "0928666622");
		        model.addAttribute("chatMessages", messages);
		    } else {
		        model.addAttribute("chatMessages", List.of()); // Gán danh sách rỗng nếu chưa đăng nhập
		    }
		model.addAttribute("productList", productDTOs); // Danh sách sản phẩm
		// Add the data to the model
		model.addAttribute("newsList", pagedNews);
		model.addAttribute("newsList", pagedNews.getContent()); // danh sách bài viết
		model.addAttribute("currentPage", page); // trang hiện tại
		model.addAttribute("totalPages", pagedNews.getTotalPages()); // tổng số trang
		return "user/news";
	}

	@GetMapping("/")
	public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    List<Category> categories = categoryRepository.findAll();

	    // Gán username nếu đã đăng nhập
	    String username = (userDetails != null) ? userDetails.getUsername() : null;
	    model.addAttribute("username", username);

	    if (username != null) {
	    	Optional<User> optionalUser = userRepository.findByUsername(username);
	    	optionalUser.ifPresent(user -> model.addAttribute("userId", user.getId()));
	        List<Message> messages = messageRepository.findChatBetweenUsers(username, "0928666622");
	        model.addAttribute("chatMessages", messages);
	    } else {
	        model.addAttribute("chatMessages", List.of()); // Gán danh sách rỗng nếu chưa đăng nhập
	    }

	    // Convert categories
	    List<CategoryDTO> categoryDTOs = categories.stream()
	            .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getImageUrl()))
	            .collect(Collectors.toList());

	    model.addAttribute("categories", categoryDTOs);
	    return "user/index";
	}

	@GetMapping("/api/categories")
	public ResponseEntity<List<CategoryDTO>> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();

		// Convert Category entities to CategoryDTO
		List<CategoryDTO> categoryDTOs = categories.stream()
				.map(category -> new CategoryDTO(category.getId(), category.getName(), category.getImageUrl() // Get the
																												// image
																												// URL
				)).collect(Collectors.toList()); // Collect and return the list of CategoryDTOs

		// Return the list of categories as JSON
		return ResponseEntity.ok(categoryDTOs); // 200 OK status with the list of CategoryDTOs
	}
}
