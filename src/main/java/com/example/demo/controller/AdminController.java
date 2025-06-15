package com.example.demo.controller;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ParentCategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.JobRecruitment;
import com.example.demo.modal.Message;
import com.example.demo.modal.News;
import com.example.demo.modal.ParentCategory;
import com.example.demo.modal.Product;
import com.example.demo.modal.ProductImage;
import com.example.demo.modal.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.JobRecruitmentService;
import com.example.demo.service.NewsService;
import com.example.demo.service.ParentCategoryService;
import com.example.demo.service.ProductService;

@Controller
public class AdminController {
	@Autowired
	private ParentCategoryService parentCategoryService;
	@Autowired
	private ParentCategoryRepository parentCategoryRepository;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JobRecruitmentService jobRecruitmentService;
	@Autowired
	MessageRepository messageRepository;
	@Autowired
	private NewsService newsService;

	@GetMapping("/quan-tri")
	public String getIndex() {
		return "admin/index";
	}
	@GetMapping("/logo")
	public String getLogo() {
		return "admin/logo";
	}
	
	@GetMapping("/quan-ly-banner")
	public String getBanner() {
		return "admin/banner";
	}

	@GetMapping("/quan-ly-don-hang")
	public String getOrder() {
		return "admin/order-manager";
	}

	@GetMapping("/quan-ly-tin-nhan")
	public String getChat(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String username = (userDetails != null) ? userDetails.getUsername() : null;
		model.addAttribute("username", username);
        model.addAttribute("loggedInUser", username);
		if (username != null) {
			Optional<User> optionalUser = userRepository.findByUsername(username);
			optionalUser.ifPresent(user -> model.addAttribute("userId", user.getId()));
			List<Message> messages = messageRepository.findChatBetweenUsers("0928666622",username );
			model.addAttribute("chatMessages", messages);
		} else {
			model.addAttribute("chatMessages", List.of()); // Gán danh sách rỗng nếu chưa đăng nhập
		}
		return "admin/chat";
	}

	@GetMapping("/quan-ly-tin-tuc")
	public String getNews(Model model) {
		List<News> allNews = newsService.getAllNews1();

		// Format the 'createdAt' date before adding it to the model
		for (News news : allNews) {
			// Assuming you want to format 'createdAt' to 'yyyy-MM-dd HH:mm:ss'
			news.setFormattedCreatedAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(news.getCreatedAt()));
		}

		// Add the data to the model
		model.addAttribute("newsList", allNews);
		return "admin/news";
	}

	@GetMapping("/quan-ly-tin-tuc/{id}")
	public String getNewsById(@PathVariable Long id, Model model) {
		News news = newsService.getNewsById(id);

		if (news != null) {
			// Format the 'createdAt' date before adding it to the model
			news.setFormattedCreatedAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(news.getCreatedAt()));

			// Add the news to the model
			model.addAttribute("news", news);
		} else {
			// Handle the case where the news with the given id is not found
			model.addAttribute("errorMessage", "News not found");
		}

		return "admin/new-edit"; // The view for displaying the detailed news
	}

	@GetMapping("/them-bai-viet")
	public String getMethodName() {
		return "admin/new-add";
	}

	@GetMapping("/quan-ly-tuyen-dung")
	public String getJobAdmin(Model model) {
		List<JobRecruitment> jobList = jobRecruitmentService.getAllJobs(); // Retrieve all jobs from the service
		model.addAttribute("jobs", jobList); // Pass the jobs list to the view
		return "admin/job-manager"; // Return the job manager view
	}

	@GetMapping("/quan-ly-khach-hang")
	public String getUsers(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<User> userPage = userRepository.findAll(pageable);
		model.addAttribute("users", userPage.getContent());
		model.addAttribute("totalPages", userPage.getTotalPages());
		model.addAttribute("currentPage", page);
		model.addAttribute("size", size);
		return "admin/user-manager";
	}

	@GetMapping("/quan-ly-san-pham")
	public String getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		// Ensure that the page index is not less than 0
		if (page < 0) {
			page = 0;
		}

		// Create Pageable with page number and page size
		Pageable pageable = PageRequest.of(page, size);

		// Fetch products with pagination
		Page<Product> productPage = productService.getAllProducts(pageable);

		// Format price to VND
		DecimalFormat decimalFormat = new DecimalFormat("#,### VND");

		// Format the prices before adding them to the model
		List<Product> products = productPage.getContent();
		for (Product product : products) {
			String formattedPrice = decimalFormat.format(product.getPrice());
			product.setFormattedPrice(formattedPrice); // You can add this field to your Product class
		}

		// Add necessary data to the model
		model.addAttribute("products", products);
		model.addAttribute("currentPage", productPage.getNumber());
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("size", size);

		return "admin/product-manager"; // Return the view name
	}

	@GetMapping("/admin/add-product")
	public String getAddProduct(Model model) {
		// Fetch all parent categories
		List<ParentCategoryDTO> parentCategories = getAllParentCategories();

		// Initialize an empty map to store child categories by parentId
		Map<Long, List<CategoryDTO>> childCategoriesMap = new HashMap<>();

		// Fetch child categories for each parent
		for (ParentCategoryDTO parentCategory : parentCategories) {
			List<CategoryDTO> childCategories = categoryService.getCategoriesByParentId(parentCategory.getId());
			childCategoriesMap.put(parentCategory.getId(), childCategories);
		}

		// Add attributes to the model
		model.addAttribute("parentCategories", parentCategories);
		model.addAttribute("childCategoriesMap", childCategoriesMap);

		return "admin/add-product";
	}

	@GetMapping("/product-details")
	public String getProductDetails(Model model) {
		// Fetch all parent categories

		return "user/product-details";
	}

	// Method to get all Parent Categories
	public List<ParentCategoryDTO> getAllParentCategories() {
		// Fetch parent categories and convert them to ParentCategoryDTO
		List<ParentCategory> parentCategories = parentCategoryRepository.findAll();

		return parentCategories.stream().map(category -> new ParentCategoryDTO(category.getId(), category.getName()))
				.collect(Collectors.toList());
	}

	@GetMapping("/quan-ly-danh-muc")
	public String getCategory(@RequestParam(defaultValue = "0") int page, // Default page number is 0
			@RequestParam(defaultValue = "10") int size, // Default page size is 10
			Model model) {

		// Fetch paginated categories
		Page<ParentCategory> parentCategoryPage = parentCategoryService.getAllParentCategories(page, size);

		// Add the page content and pagination info to the model
		model.addAttribute("categories", parentCategoryPage.getContent()); // List of categories for current page
		model.addAttribute("currentPage", page); // Current page number
		model.addAttribute("totalPages", parentCategoryPage.getTotalPages()); // Total number of pages
		model.addAttribute("totalItems", parentCategoryPage.getTotalElements()); // Total number of items

		return "admin/category"; // Return the view that will be rendered
	}
}
