package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Product;
import com.example.demo.repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/products")
public class ProductControllerUser {
	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/suggested-products")
	public List<ProductDTO> getSuggestedProducts(@RequestParam(defaultValue = "1") Long categoryId,
			HttpServletRequest request) {
		List<Product> products = productRepository.findTodayProductsByCategory(categoryId);
		String baseUrl = getBaseUrl(request);
		return products.stream().map(product -> {
			ProductDTO dto = new ProductDTO();
			dto.setId(product.getId());
			dto.setName(product.getName());
			dto.setPrice(product.getPrice());
			dto.setDescription(product.getDescription());
			dto.setSubDescription(product.getSubDescription());
			dto.setDiscount(product.getDiscount());
			dto.setCreatedDate(product.getCreatedDate());
			dto.setImageUrl(product.getImageUrl());
			dto.setProductCode(product.getProductCode());
			dto.setBrand(product.getBrand());
			dto.setCurrency(product.getCurrency());
			dto.setValidUntil(product.getValidUntil());
			dto.setRating(product.getRating());
			dto.setSlug(product.getSlug());
			dto.setUrl(baseUrl + "/chi-tiet-san-pham/" + product.getSlug());
			dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
			dto.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
			return dto;
		}).collect(Collectors.toList());
	}

	@GetMapping("/new-today")
	public List<ProductDTO> getProductsCreatedTodayNotDeleted(HttpServletRequest request) {
		List<Product> products = productRepository.findProductsCreatedTodayNotDeleted();
		String baseUrl = getBaseUrl(request);
		// Convert to DTO and remove unnecessary recursion
		List<ProductDTO> productDTOs = products.stream().map(product -> {
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
			productDTO.setUrl(baseUrl + "/chi-tiet-san-pham/" + product.getSlug());
			productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
			productDTO.setParentCategoryId(
					product.getParentCategory() != null ? product.getParentCategory().getId() : null);
			return productDTO;
		}).collect(Collectors.toList());

		return productDTOs;
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
	@GetMapping("/by-category")
	public Page<ProductDTO> getProductsByCategory(@RequestParam(required = false) Long parentCategoryId,
			@RequestParam(required = false) Long categoryId, @RequestParam(required = false) String search, // Tìm kiếm
																											// theo tên
																											// sản phẩm
			@RequestParam(defaultValue = "0") int page, // Default to page 0
			@RequestParam(defaultValue = "12") int size, HttpServletRequest request) { // Default to 12 products per
																						// page
		Page<Product> products;

		Pageable pageable = PageRequest.of(page, size); // Create Pageable instance

		if (parentCategoryId != null && search != null && !search.isEmpty()) {
			// Tìm sản phẩm theo category cha và từ khóa tìm kiếm
			products = productRepository.findByParentCategoryIdAndNameContainingAndDeletedFalse(parentCategoryId,
					search, pageable);
		} else if (categoryId != null && search != null && !search.isEmpty()) {

			products = productRepository.findByCategoryIdAndNameContainingAndDeletedFalse(categoryId, search, pageable);
		} else if (parentCategoryId != null) {

			products = productRepository.findByParentCategoryIdAndDeletedFalse(parentCategoryId, pageable);
		} else if (categoryId != null) {

			products = productRepository.findByCategoryIdAndDeletedFalse(categoryId, pageable);
		} else {

			products = productRepository.findAllByNameContainingAndDeletedFalse(search, pageable);
		}
		String baseUrl = getBaseUrl(request);

		Page<ProductDTO> productDTOs = products.map(product -> {
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
			productDTO.setUrl(baseUrl + "/chi-tiet-san-pham/" + product.getSlug());
			productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
			productDTO.setParentCategoryId(
					product.getParentCategory() != null ? product.getParentCategory().getId() : null);
			return productDTO;
		});

		return productDTOs; // Trả về kết quả đã phân trang
	}

}
