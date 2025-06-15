package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.BreadcrumbItem;
import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.Logo;
import com.example.demo.modal.Message;
import com.example.demo.modal.ParentCategory;
import com.example.demo.modal.Product;
import com.example.demo.modal.ProductImage;
import com.example.demo.modal.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LogoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductDetailController {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private LogoService logoService;

	@GetMapping("/chi-tiet-san-pham/{slug}")
	public String getProductById(@PathVariable String slug, Model model,
			@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {

		Optional<Product> optionalProduct = productRepository.findBySlugAndDeletedFalse(slug);
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
		if (optionalProduct.isPresent()) {

			Product product = optionalProduct.get();

			ProductDTO productDTO = new ProductDTO();
			productDTO.setId(product.getId());
			productDTO.setName(product.getName());
			productDTO.setPrice(product.getPrice());
			productDTO.setDescription(product.getDescription());
			productDTO.setSubDescription(product.getSubDescription());
			productDTO.setDiscount(product.getDiscount());
			productDTO.setCreatedDate(product.getCreatedDate());
			productDTO.setImageUrl(baseUrl + "/loadImage?imageName=" + product.getImageUrl());
			productDTO.setProductCode(product.getProductCode());
			productDTO.setBrand(product.getBrand());
			productDTO.setCurrency(product.getCurrency());
			productDTO.setValidUntil(product.getValidUntil());
			productDTO.setRating(product.getRating());
			productDTO.setSlug(product.getSlug());
			String cleanDescription = Jsoup.parse(product.getDescription()).text();
			productDTO.setDescriptionSeo(cleanDescription);
			productDTO.setUrl(baseUrl + "/chi-tiet-san-pham/" + product.getSlug());
			productDTO.setTitle(product.getTitle());
			// Set category and parentCategory
			productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
			productDTO.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);

			productDTO.setParentCategoryId(
					product.getParentCategory() != null ? product.getParentCategory().getId() : null);
			productDTO.setParentCategoryName(
					product.getParentCategory() != null ? product.getParentCategory().getName() : null);
			List<String> imageUrls = product.getProductImages().stream().map(ProductImage::getImageUrl)
					.collect(Collectors.toList());
			productDTO.setProductImageUrls(imageUrls);
			// Convert the list of similar products to ProductDTOs
			List<Product> similarProducts = productRepository.findByCategoryIdOrParentCategoryIdAndIdNot(
					product.getCategory() != null ? product.getCategory().getId() : null,
					product.getParentCategory() != null ? product.getParentCategory().getId() : null, product.getId() // Exclude
			// product
			);

			String categoryPath = getFullCategoryUrl(request, product.getCategory(), product.getParentCategory());
			productDTO.setCategoryPath(categoryPath);
			List<BreadcrumbItem> breadcrumbs = new ArrayList<>();

			breadcrumbs.add(new BreadcrumbItem(1, "Trang chủ", getBaseUrl(request)));

			if (product.getParentCategory() != null) {
				String parentUrl = getBaseUrl(request) + "/" + encodeUrlSegment(product.getParentCategory().getName());
				breadcrumbs.add(new BreadcrumbItem(2, product.getParentCategory().getName(), parentUrl));
			}

			if (product.getCategory() != null) {
				String categoryUrl = getBaseUrl(request) + "/"
						+ encodeUrlSegment(
								product.getParentCategory() != null ? product.getParentCategory().getName() : "")
						+ "/" + encodeUrlSegment(product.getCategory().getName());
				breadcrumbs.add(new BreadcrumbItem(3, product.getCategory().getName(), categoryUrl));
			}

			breadcrumbs.add(new BreadcrumbItem(4, product.getName(),
					getBaseUrl(request) + "/chi-tiet-san-pham/" + product.getSlug()));

			// --- Chèn đoạn tạo JSON cho breadcrumbs ---
			ObjectMapper objectMapper = new ObjectMapper();

			List<Map<String, Object>> breadcrumbList = breadcrumbs.stream().map(b -> Map.of("@type", "ListItem",
					"position", b.getPosition(), "item", Map.of("@id", b.getUrl(), "name", b.getName())))
					.collect(Collectors.toList());

			String breadcrumbsJson = "";
			try {
				breadcrumbsJson = objectMapper.writeValueAsString(breadcrumbList);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			model.addAttribute("breadcrumbsJson", breadcrumbsJson);

			Map<String, Object> productSchema = new HashMap<>();
			productSchema.put("@context", "https://schema.org");
			productSchema.put("@type", "Product");
			productSchema.put("name", productDTO.getName());
			productSchema.put("image", productDTO.getImageUrl());
			productSchema.put("description", productDTO.getDescriptionSeo());
			productSchema.put("sku", productDTO.getProductCode());

			Map<String, Object> brand = Map.of("@type", "Brand", "name", productDTO.getBrand());
			productSchema.put("brand", brand);

			Map<String, Object> offer = new HashMap<>();
			offer.put("@type", "Offer");
			offer.put("url", productDTO.getUrl());
			offer.put("priceCurrency", "VND");
			offer.put("price", productDTO.getPrice() != null ? productDTO.getPrice() : 0);
			offer.put("priceValidUntil", productDTO.getValidUntil());
			offer.put("availability", "InStock");
			offer.put("discount", productDTO.getDiscount());
			productSchema.put("offers", offer);

			Map<String, Object> reviewRating = Map.of("@type", "Rating", "ratingValue", productDTO.getRating(),
					"worstRating", 1, "bestRating", 5);

			Map<String, Object> review = Map.of("@type", "Review", "author",
					Map.of("@type", "Person", "name", "USS Dược Phẩm"), "reviewRating", reviewRating);
			productSchema.put("review", review);

			Map<String, Object> manufacturer = new HashMap<>();
			manufacturer.put("@type", "Organization");
			manufacturer.put("@id", baseUrl + "/#organization");
			manufacturer.put("name", "Công ty TNHH Dược Phẩm Quốc tế USS");
			manufacturer.put("url", baseUrl);
			manufacturer.put("logo",
					Map.of("@type", "ImageObject", "@id", baseUrl + "/#logo", "url", logoUrl, "contentUrl", logoUrl,
							"caption", "Công ty TNHH Dược Phẩm Quốc tế USS", "inLanguage", "vi", "width", "501",
							"height", "323"));
			manufacturer.put("address",
					Map.of("@type", "PostalAddress", "streetAddress", "157 Đống Đa, phường Thị Nại", "addressLocality",
							"TP. Quy Nhơn", "addressRegion", "Bình Định", "postalCode", "55000", "addressCountry",
							"VN"));
			manufacturer.put("openingHoursSpecification",
					List.of(Map.of("@type", "OpeningHoursSpecification", "dayOfWeek",
							List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
							"opens", "09:00", "closes", "17:00")));
			productSchema.put("manufacturer", manufacturer);

			// Convert to JSON-LD string and pass to model
			try {
				ObjectMapper mapper = new ObjectMapper();
				String schemaJson = mapper.writeValueAsString(productSchema);
				model.addAttribute("productSchemaJson", schemaJson);
			} catch (JsonProcessingException e) {
				model.addAttribute("productSchemaJson", "{}");
			}
			List<ProductDTO> similarProductDTOs = similarProducts.stream().map(this::convertToDTO) // Convert each
																									// product to DTO
					.collect(Collectors.toList());

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
			model.addAttribute("similarProducts", similarProductDTOs);
			// Add the productDTO to the model to be accessed in the view
			model.addAttribute("product", productDTO);

			// Return the view name (admin/add-product)
			return "user/product-details";
		} else {
			// If product not found, you can redirect to a different view or return an error
			// page
			return "/";
		}
	}

	public String getFullCategoryUrl(HttpServletRequest request, Category category, ParentCategory parentCategory) {
		// Lấy baseUrl theo header X-Forwarded-* hoặc fallback
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
		if ((scheme.equals("http") && !"80".equals(port) && !"8080".equals(port))
				|| (scheme.equals("https") && !"443".equals(port))) {
			baseUrl += ":" + port;
		}

		// Xây dựng phần path
		String path = "";
		if (category != null) {
			ParentCategory parent = category.getParentCategory();
			if (parent != null) {
				path = "/" + encodeUrlSegment(parent.getName()) + "/" + encodeUrlSegment(category.getName());
			} else {
				path = "/" + encodeUrlSegment(category.getName());
			}
		} else if (parentCategory != null) {
			path = "/" + encodeUrlSegment(parentCategory.getName());
		}

		// Trả về URL đầy đủ
		return baseUrl + path;
	}

	private String encodeUrlSegment(String input) {
		if (input == null)
			return "";

		String noAccents = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return noAccents.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
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
		productDTO.setProductCode(product.getProductCode());
		productDTO.setBrand(product.getBrand());
		productDTO.setCurrency(product.getCurrency());
		productDTO.setValidUntil(product.getValidUntil());
		productDTO.setRating(product.getRating());
		productDTO.setSlug(product.getSlug());
		productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
		productDTO
				.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
		productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
		productDTO.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);

		productDTO
				.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
		productDTO.setParentCategoryName(
				product.getParentCategory() != null ? product.getParentCategory().getName() : null);
		return productDTO;
	}
}
