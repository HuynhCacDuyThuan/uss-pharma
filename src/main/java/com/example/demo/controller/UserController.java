package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import com.example.demo.modal.Banner;
import com.example.demo.modal.Category;
import com.example.demo.modal.JobRecruitment;
import com.example.demo.modal.Logo;
import com.example.demo.modal.Message;
import com.example.demo.modal.News;
import com.example.demo.modal.Product;
import com.example.demo.modal.User;
import com.example.demo.repository.BannerRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ForgotPasswordService;
import com.example.demo.service.JobRecruitmentService;
import com.example.demo.service.LogoService;
import com.example.demo.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
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
	@Autowired
	private LogoService logoService;

	@Autowired
	private BannerRepository bannerRepository;

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

	@GetMapping("/tuyen-dung/")
	public String job(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
		List<JobRecruitment> jobList = jobRecruitmentService.getAllJobs(); // Retrieve all jobs from the service
		List<Logo> logos = logoService.getAllLogos();
		String baseUrl = getBaseUrl(request);

		String logoUrl = null;

		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

		}

		model.addAttribute("logoUrl", logoUrl);
		String username = (userDetails != null) ? userDetails.getUsername() : null;
		model.addAttribute("username", username);

		if (username != null) {
			Optional<User> optionalUser = userRepository.findByUsername(username);
			optionalUser.ifPresent(user -> model.addAttribute("userId", user.getId()));
			List<Message> messages = messageRepository.findChatBetweenUsers(username, "0928666622");
			model.addAttribute("chatMessages", messages);
		} else {
			model.addAttribute("chatMessages", List.of());
		}
		model.addAttribute("jobs", jobList);
		return "user/job";
	}

	@GetMapping("/chi-tiet-bai-viet/{title}")
	public String getNewsByTitle(Model model, @PathVariable String title, HttpServletRequest request) {
		// Ensure the title is URL-decoded if necessary, in case it was encoded (e.g.,
		// %20 for spaces).
		try {
			// Decode title (if encoded in the URL) – this step may not be necessary if it's
			// already handled elsewhere.
			String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8.name());

			List<Logo> logos = logoService.getAllLogos();
			String baseUrl = getBaseUrl(request);

			String logoUrl = null;

			if (logos != null && !logos.isEmpty()) {
				// Lấy logo đầu tiên
				Logo firstLogo = logos.get(0);

				logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

			}

			// Thêm logoUrl vào model để truyền ra view
			model.addAttribute("logoUrl", logoUrl);
			// Fetch news by the decoded title
			News news = newsService.getNewsByTitle(decodedTitle);
			List<News> top5 = newsService.getTop5News();
			model.addAttribute("top5News", top5);
			if (news != null) {
				// Format the 'createdAt' date before adding it to the model
				news.setFormattedCreatedAt(
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(news.getCreatedAt()));
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

	@GetMapping("/tin-tuc/")
	public String getNews(Model model, @AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, HttpServletRequest request) {

		Pageable pageable = PageRequest.of(0, 3); // Trang 0, lấy 5 sản phẩm
		Pageable pageable1 = PageRequest.of(page, 4); // Trang 0, lấy 5 sản phẩm
		List<Logo> logos = logoService.getAllLogos();
		String baseUrl = getBaseUrl(request);

		String logoUrl = null;

		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

		}
		String url = baseUrl + "/tin-tuc/";
		model.addAttribute("url", url);
		Map<String, Object> logo = Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url", logoUrl,
				"contentUrl", logoUrl, "caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi", "width",
				"501", "height", "323");

		Map<String, Object> organization = Map.of("@type", "CommunityHealth", "@id", baseUrl + "/#organization", "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "url", baseUrl, "logo", logo, "openingHours",
				List.of("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday 09:00-17:00"));

		Map<String, Object> website = Map.of("@type", "WebSite", "@id", baseUrl + "/#website", "url", baseUrl, "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "alternateName", "USS PHARMA", "publisher",
				Map.of("@id", baseUrl + "/#organization"), "inLanguage", "vi");

		Map<String, Object> breadcrumb = Map.of("@type", "BreadcrumbList", "@id", baseUrl + "/tin-tuc/#breadcrumb",
				"itemListElement",
				List.of(Map.of("@type", "ListItem", "position", 1, "item", Map.of("@id", baseUrl, "name", "Home")),
						Map.of("@type", "ListItem", "position", 2, "item",
								Map.of("@id", baseUrl + "/tin-tuc/", "name", "TIN TỨC"))));

		Map<String, Object> webpage = Map.of("@type", "CollectionPage", "@id", baseUrl + "/tin-tuc/#webpage", "url",
				baseUrl + "/tin-tuc/", "name", "TIN TỨC - Công ty TNHH Dược Phẩm Quốc tế USS", "isPartOf",
				Map.of("@id", baseUrl + "/#website"), "inLanguage", "vi", "breadcrumb",
				Map.of("@id", baseUrl + "/tin-tuc/#breadcrumb"));

		// JSON-LD root
		Map<String, Object> jsonLd = Map.of("@context", "https://schema.org", "@graph",
				List.of(organization, website, breadcrumb, webpage));

		// Convert to JSON string
		try {
			ObjectMapper mapper = new ObjectMapper();
			String schemaJson = mapper.writeValueAsString(jsonLd);
			model.addAttribute("schemaJson", schemaJson);
		} catch (JsonProcessingException e) {
			model.addAttribute("schemaJson", "{}");
		}
		model.addAttribute("logoUrl", logoUrl);
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
		pagedNews.forEach(news -> news
				.setFormattedCreatedAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(news.getCreatedAt())));
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
	public String home(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
		List<Category> categories = categoryRepository.findAll();
		String baseUrl = getBaseUrl(request);
		List<Logo> logos = logoService.getAllLogos();
		List<Banner> banners = bannerRepository.findAll();
		model.addAttribute("banners", banners);
		String logoUrl = null;
		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

		}
		Banner secondBanner = banners.get(1); // Lấy banner thứ 2 (index 1)
		String imageUrl = baseUrl + "/loadImage?imageName=" + secondBanner.getImageUrl();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> logo = Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url",  logoUrl,
				"contentUrl",  logoUrl, "caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi",
				"width", "501", "height", "323");

		// ORGANIZATION
		Map<String, Object> organization = Map.of("@type", List.of("CommunityHealth", "Organization"), "@id",
				baseUrl + "/#organization", "name", "Công ty TNHH Dược Phẩm Quốc tế USS", "url", baseUrl, "logo", logo,
				"openingHours", List.of("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday 09:00-17:00"),
				"address",
				Map.of("@type", "PostalAddress", "streetAddress", "157 Đống Đa, phường Thị Nại", "addressLocality",
						"TP. Quy Nhơn", "addressRegion", "Bình Định", "postalCode", "55000", "addressCountry", "VN"));

		// WEBSITE
		Map<String, Object> website = Map.of("@type", "WebSite", "@id", baseUrl + "/#website", "url", baseUrl, "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "alternateName", "USS PHARMA", "publisher",
				Map.of("@id", baseUrl + "/#organization"), "inLanguage", "vi");

		// IMAGE
		Map<String, Object> imageObject = Map.of("@type", "ImageObject", "@id", imageUrl, "url", imageUrl, "width",
				"200", "height", "200", "inLanguage", "vi");

		// WEBPAGE
		Map<String, Object> webpage = Map.of("@type", "WebPage", "@id", baseUrl + "/#webpage", "url", baseUrl + "/",
				"name", "Trang chủ - Công ty TNHH Dược Phẩm Quốc tế USS", "datePublished", "2023-11-20T03:11:17+07:00",
				"dateModified", "2025-04-28T21:50:46+07:00", "about", Map.of("@id", baseUrl + "/#organization"),
				"isPartOf", Map.of("@id", baseUrl + "/#website"), "primaryImageOfPage", Map.of("@id", imageUrl),
				"inLanguage", "vi");

		// ARTICLE
		Map<String, Object> article = new HashMap<>();
		article.put("@type", "Article");
		article.put("headline", "Trang chủ - Công ty TNHH Dược Phẩm Quốc tế USS");
		article.put("datePublished", "2023-11-20T03:11:17+07:00");
		article.put("dateModified", "2025-04-28T21:50:46+07:00");
		article.put("description",
				"Với hơn 10 năm kinh nghiệm trong lĩnh vực phân phối Dược phẩm, Thực phẩm dinh dưỡng Y tế cao cấp.Ngành nghề kinh doanh: Mua bán thực phẩm chức năng, yến sào và thực phẩm khác; Bán buôn dược phẩm và dụng cụ y tế;");
		article.put("name", "Trang chủ - Công ty TNHH Dược Phẩm Quốc tế USS");
		article.put("@id", baseUrl + "/#richSnippet");
		article.put("isPartOf", Map.of("@id", baseUrl + "/#webpage"));
		article.put("image", Map.of("@id", imageUrl));
		article.put("inLanguage", "vi");
		article.put("mainEntityOfPage", Map.of("@id", baseUrl + "/#webpage"));

		// BUILD JSON-LD
		Map<String, Object> jsonLd = Map.of("@context", "https://schema.org", "@graph",
				List.of(organization, website, imageObject, webpage, article));

		try {
			String schemaJson = mapper.writeValueAsString(jsonLd);
			model.addAttribute("schemaJson", schemaJson);
		} catch (JsonProcessingException e) {
			model.addAttribute("schemaJson", "{}"); // hoặc log lỗi nếu cần
			e.printStackTrace(); // Tùy chọn
		}
		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

		}
		model.addAttribute("baseUrl", baseUrl);
		// Thêm logoUrl vào model để truyền ra view
		model.addAttribute("logoUrl", logoUrl);
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

//		// Nếu port không phải mặc định thì thêm vào URL
//		if ((scheme.equals("http") && !"80".equals(port)) || (scheme.equals("https") && !"443".equals(port))) {
//			baseUrl += ":" + port;
//		}

		return baseUrl;
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
