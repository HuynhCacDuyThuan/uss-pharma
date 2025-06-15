package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ParentCategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.Logo;
import com.example.demo.modal.Message;
import com.example.demo.modal.ParentCategory;
import com.example.demo.modal.Product;
import com.example.demo.modal.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.LogoService;
import com.example.demo.service.ParentCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
	@Autowired
	private ParentCategoryService parentCategoryService;
	@Autowired
	private ParentCategoryRepository parentCategoryRepository;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private LogoService logoService;

	@GetMapping("/gioi-thieu/")
	public String getAboutPage(Model model, @AuthenticationPrincipal UserDetails userDetails,
			HttpServletRequest request) {

		String username = (userDetails != null) ? userDetails.getUsername() : null;
		model.addAttribute("username", username);

		List<Logo> logos = logoService.getAllLogos();
		String baseUrl = getBaseUrl(request);

		String logoUrl = null;
		String logoUrl2 = null;
		String logoUrl3 = null;
		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();
			if (logos.size() > 1) {
				Logo secondLogo = logos.get(1);
				logoUrl2 = baseUrl + "/loadImage?imageName=" + secondLogo.getImageUrl();
			}
			if (logos.size() > 2) {
				Logo thirdLogo = logos.get(2);
				logoUrl3 = baseUrl + "/loadImage?imageName=" + thirdLogo.getImageUrl();
			}
		}

		Map<String, Object> logo = Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url", logoUrl,
				"contentUrl", logoUrl, "caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi", "width",
				"501", "height", "323");
		model.addAttribute("logoUrl3", logoUrl3);
		Map<String, Object> organization = new LinkedHashMap<>();
		organization.put("@type", List.of("CommunityHealth", "Organization"));
		organization.put("@id", baseUrl + "/#organization");
		organization.put("name", "Công ty TNHH Dược Phẩm Quốc tế USS");
		organization.put("url", baseUrl);
		organization.put("logo", logo);
		organization.put("openingHours",
				List.of("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday 09:00-17:00"));
		organization.put("address",
				Map.of("@type", "PostalAddress", "streetAddress", "157 Đống Đa, phường Thị Nại", "addressLocality",
						"TP. Quy Nhơn", "addressRegion", "Bình Định", "postalCode", "55000", "addressCountry", "VN"));

		Map<String, Object> website = Map.of("@type", "WebSite", "@id", baseUrl + "/#website", "url", baseUrl, "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "alternateName", "USS PHARMA", "publisher",
				Map.of("@id", baseUrl + "/#organization"), "inLanguage", "vi");

		Map<String, Object> breadcrumb = Map.of("@type", "BreadcrumbList", "@id", baseUrl + "/gioi-thieu/#breadcrumb",
				"itemListElement",
				List.of(Map.of("@type", "ListItem", "position", "1", "item", Map.of("@id", baseUrl, "name", "Home")),
						Map.of("@type", "ListItem", "position", "2", "item",
								Map.of("@id", baseUrl + "/gioi-thieu/", "name", "Giới thiệu"))));

		Map<String, Object> webpage = Map.of("@type", "WebPage", "@id", baseUrl + "/gioi-thieu/#webpage", "url",
				baseUrl + "/gioi-thieu/", "name", "Giới thiệu - Công ty TNHH Dược Phẩm Quốc tế USS", "datePublished",
				"2023-11-20T03:25:53+07:00", "dateModified", "2025-03-25T09:19:12+07:00", "isPartOf",
				Map.of("@id", baseUrl + "/#website"), "primaryImageOfPage", Map.of("@id", logoUrl2), "inLanguage", "vi",
				"breadcrumb", Map.of("@id", baseUrl + "/gioi-thieu/#breadcrumb"));

		Map<String, Object> article = new LinkedHashMap<>();
		article.put("@type", "Article");
		article.put("headline", "Giới thiệu - Công ty TNHH Dược Phẩm Quốc tế USS");
		article.put("datePublished", "2023-11-20T03:25:53+07:00");
		article.put("dateModified", "2025-03-25T09:19:12+07:00");
		article.put("publisher", Map.of("@id", baseUrl + "/#organization"));
		article.put("description", "Công ty TNHH Dược phẩm Quốc tế USS với hơn 10 năm kinh nghiệm...");
		article.put("name", "Giới thiệu - Công ty TNHH Dược Phẩm Quốc tế USS");
		article.put("@id", baseUrl + "/gioi-thieu/#richSnippet");
		article.put("isPartOf", Map.of("@id", baseUrl + "/gioi-thieu/#webpage"));
		article.put("image", Map.of("@id", logoUrl2));
		article.put("inLanguage", "vi");
		article.put("mainEntityOfPage", Map.of("@id", baseUrl + "/gioi-thieu/#webpage"));

		List<Object> graph = List.of(organization, website, breadcrumb, webpage, article);

		Map<String, Object> jsonLd = Map.of("@context", "https://schema.org", "@graph", graph);

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String schemaJson = mapper.writeValueAsString(jsonLd);
			model.addAttribute("schemaJson", schemaJson);
		} catch (JsonProcessingException e) {
			model.addAttribute("schemaJson", "{}");
		}

		model.addAttribute("logoUrl", logoUrl);
		model.addAttribute("logoUrl3", logoUrl3);
		String urlLienHe = baseUrl + "/gioi-thieu/";
		model.addAttribute("urlLienHe", urlLienHe);
		if (username != null) {
			Optional<User> optionalUser = userRepository.findByUsername(username);
			optionalUser.ifPresent(user -> model.addAttribute("userId", user.getId()));
			List<Message> messages = messageRepository.findChatBetweenUsers(username, "0928666622");
			model.addAttribute("chatMessages", messages);
		} else {
			model.addAttribute("chatMessages", List.of()); // Gán danh sách rỗng nếu chưa đăng nhập
		}
		return "user/about"; // Assuming the about page is named 'about.html'
	}

	@GetMapping("/lien-he/")
	public String getContact(Model model, @AuthenticationPrincipal UserDetails userDetails,
			HttpServletRequest request) {
		List<Logo> logos = logoService.getAllLogos();
		String baseUrl = getBaseUrl(request);

		String logoUrl = null;

		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();
			
		}
		String url = baseUrl + "/lien-he/";
		model.addAttribute("url", url);
		Map<String, Object> logo = Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url", logoUrl,
				"contentUrl", logos, "caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi", "width",
				"501", "height", "323");

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

		// BREADCRUMB
		Map<String, Object> breadcrumb = Map.of("@type", "BreadcrumbList", "@id", baseUrl + "/lien-he/#breadcrumb",
				"itemListElement",
				List.of(Map.of("@type", "ListItem", "position", 1, "item", Map.of("@id", baseUrl, "name", "Home")),
						Map.of("@type", "ListItem", "position", 2, "item",
								Map.of("@id", baseUrl + "/lien-he/", "name", "Liên hệ"))));

		// WEBPAGE
		Map<String, Object> webpage = Map.of("@type", "WebPage", "@id", baseUrl + "/lien-he/#webpage", "url",
				baseUrl + "/lien-he/", "name", "Liên hệ - Công ty TNHH Dược Phẩm Quốc tế USS", "datePublished",
				"2023-11-20T03:26:09+07:00", "dateModified", "2023-11-23T06:44:40+07:00", "isPartOf",
				Map.of("@id", baseUrl + "/#website"), "primaryImageOfPage", Map.of("@id", logoUrl), "inLanguage", "vi",
				"breadcrumb", Map.of("@id", baseUrl + "/lien-he/#breadcrumb"));

		// ARTICLE
		Map<String, Object> article = new HashMap<>();
		article.put("@type", "Article");
		article.put("headline", "Liên hệ - Công ty TNHH Dược Phẩm Quốc tế USS");
		article.put("datePublished", "2023-11-20T03:26:09+07:00");
		article.put("dateModified", "2023-11-23T06:44:40+07:00");
		article.put("publisher", Map.of("@id", baseUrl + "/#organization"));
		article.put("description",
				"Địa chỉ: 157 Đồng Đa, phường Thị Nại, TP. Quy Nhơn, Bình Định Hotline: 090 357 77 27 Email: uss.intl.pharma@gmail.com");
		article.put("name", "Liên hệ - Công ty TNHH Dược Phẩm Quốc tế USS");
		article.put("@id", baseUrl + "/lien-he/#richSnippet");
		article.put("isPartOf", Map.of("@id", baseUrl + "/lien-he/#webpage"));
		article.put("image", Map.of("@id", logoUrl));
		article.put("inLanguage", "vi");
		article.put("mainEntityOfPage", Map.of("@id", baseUrl + "/lien-he/#webpage"));
		// GRAPH & CONTEXT
		Map<String, Object> jsonLd = Map.of("@context", "https://schema.org", "@graph",
				List.of(organization, website, breadcrumb, webpage, article));

		try {
			ObjectMapper mapper = new ObjectMapper();
			String schemaJson = mapper.writeValueAsString(jsonLd);
			model.addAttribute("schemaJson", schemaJson);
		} catch (Exception e) {
			model.addAttribute("schemaJson", "{}");
		}
		model.addAttribute("logoUrl", logoUrl);
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
		return "user/contact"; // Assuming the about page is named 'about.html'
	}

	@GetMapping("/san-pham/")
	public String getProductByCategoryName(
			@RequestParam(value = "categoryIds", required = false) List<Long> categoryIds, Model model,
			@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
		List<ParentCategoryDTO> parentCategories = getAllParentCategories();
		List<Logo> logos = logoService.getAllLogos();
		String baseUrl = getBaseUrl(request);
		List<Map<String, Object>> graph = new ArrayList<>();

		String logoUrl = null;

		if (logos != null && !logos.isEmpty()) {
			// Lấy logo đầu tiên
			Logo firstLogo = logos.get(0);

			logoUrl = baseUrl + "/loadImage?imageName=" + firstLogo.getImageUrl();

		}
		String url = baseUrl + "/san-pham/";
		model.addAttribute("url", url);

		// Logo
		Map<String, Object> logo = Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url",  logoUrl,
				"contentUrl", logoUrl, "caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi", "width",
				"501", "height", "323");

		// Địa chỉ
		Map<String, Object> address = Map.of("@type", "PostalAddress", "streetAddress", "157 Đống Đa, phường Thị Nại",
				"addressLocality", "TP. Quy Nhơn", "addressRegion", "Bình Định", "postalCode", "55000",
				"addressCountry", "VN");

		// Tổ chức (Organization)
		Map<String, Object> organization = Map.of("@type", "CommunityHealth", "@id", baseUrl + "/#organization", "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "url", baseUrl, "logo", logo, "address", address, "openingHours",
				List.of("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday 09:00-17:00"));

		// Website
		Map<String, Object> website = Map.of("@type", "WebSite", "@id", baseUrl + "/#website", "url", baseUrl, "name",
				"Công ty TNHH Dược Phẩm Quốc tế USS", "alternateName", "USS PHARMA", "publisher",
				Map.of("@id", baseUrl + "/#organization"), "inLanguage", "vi");

		// BreadcrumbList
		Map<String, Object> breadcrumbList = Map.of("@type", "BreadcrumbList", "@id", baseUrl + "/san-pham/#breadcrumb",
				"itemListElement",
				List.of(Map.of("@type", "ListItem", "position", "1", "item", Map.of("@id", baseUrl, "name", "Home")),
						Map.of("@type", "ListItem", "position", "2", "item",
								Map.of("@id", baseUrl + "/san-pham/", "name", "Sản phẩm"))));

		// CollectionPage
		Map<String, Object> collectionPage = Map.of("@type", "CollectionPage", "@id", baseUrl + "/san-pham/#webpage",
				"url", baseUrl + "/san-pham/", "name", "Sản phẩm - Công ty TNHH Dược Phẩm Quốc tế USS", "isPartOf",
				Map.of("@id", baseUrl + "/#website"), "inLanguage", "vi", "breadcrumb",
				Map.of("@id", baseUrl + "/san-pham/#breadcrumb"));

		// Add tất cả vào @graph
		graph.add(organization);
		graph.add(website);
		graph.add(breadcrumbList);
		graph.add(collectionPage);

		Map<String, Object> jsonLd = Map.of("@context", "https://schema.org", "@graph", graph);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String schemaJson = objectMapper.writeValueAsString(jsonLd);
			model.addAttribute("schemaJson", schemaJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			model.addAttribute("schemaJson", "{}");
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
			model.addAttribute("chatMessages", List.of()); // Gán danh sách rỗng nếu chưa đăng nhập
		}
		Map<Long, List<CategoryDTO>> childCategoriesMap = new HashMap<>();
		Map<Long, List<ProductDTO>> productMap = new HashMap<>();

		for (ParentCategoryDTO parentCategory : parentCategories) {
			List<CategoryDTO> childCategories = categoryService.getCategoriesByParentId(parentCategory.getId());

			List<ProductDTO> filteredProducts = new ArrayList<>();
			if (categoryIds == null || categoryIds.isEmpty()) {
				// Fetch products for all categories if none are selected
				filteredProducts = fetchProductsForCategory(parentCategory, childCategories);
			} else {
				// Filter products based on selected category IDs
				if (categoryIds.contains(parentCategory.getId())) {
					filteredProducts.addAll(fetchProductsForCategory(parentCategory, childCategories));
				}
				for (CategoryDTO childCategory : childCategories) {
					if (categoryIds.contains(childCategory.getId())) {
						filteredProducts.addAll(fetchProductsForChildCategory(childCategory));
					}
				}
			}

			if (!filteredProducts.isEmpty()) {
				productMap.put(parentCategory.getId(), filteredProducts);
			}

			childCategoriesMap.put(parentCategory.getId(), childCategories);
		}

		model.addAttribute("parentCategories", parentCategories);
		model.addAttribute("childCategoriesMap", childCategoriesMap);
		model.addAttribute("productMap", productMap);

		return "user/product";
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

	private List<ProductDTO> fetchProductsForCategory(ParentCategoryDTO parentCategory,
			List<CategoryDTO> childCategories) {
		List<Product> products = productRepository.findByCategoryIdOrParentCategoryId(parentCategory.getId(),
				parentCategory.getId());
		return products.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private List<ProductDTO> fetchProductsForChildCategory(CategoryDTO childCategory) {
		List<Product> products = productRepository.findByCategoryIdOrParentCategoryId(childCategory.getId(),
				childCategory.getParentCategoryId());
		return products.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private ProductDTO convertToDTO(Product product) {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setPrice(product.getPrice());
		productDTO.setDescription(product.getDescription());
		productDTO.setSubDescription(product.getSubDescription());
		productDTO.setDiscount(product.getDiscount());
		productDTO.setCreatedDate(product.getCreatedDate());
		productDTO.setImageUrl(product.getImageUrl());
		productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
		productDTO
				.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
		return productDTO;
	}

	public List<ParentCategoryDTO> getAllParentCategories() {
		// Fetch parent categories and convert them to ParentCategoryDTO
		List<ParentCategory> parentCategories = parentCategoryRepository.findAll();

		return parentCategories.stream().map(category -> new ParentCategoryDTO(category.getId(), category.getName()))
				.collect(Collectors.toList());
	}
}
