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

@RestController
@RequestMapping("/api/products")
public class ProductControllerUser {
	@Autowired
    private ProductRepository productRepository;

	@GetMapping("/suggested-products")
	public List<ProductDTO> getSuggestedProducts(@RequestParam(defaultValue = "1") Long categoryId) {
	    List<Product> products = productRepository.findTodayProductsByCategory(categoryId);

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
	        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
	        dto.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
	        return dto;
	    }).collect(Collectors.toList());
	}

	@GetMapping("/new-today")
	public List<ProductDTO> getProductsCreatedTodayNotDeleted() {
	    List<Product> products = productRepository.findProductsCreatedTodayNotDeleted();

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
	        // Set category and parentCategory
	        productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
	        productDTO.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
	        return productDTO;
	    }).collect(Collectors.toList());

	    return productDTOs;
	}
	@GetMapping("/by-category")
	public Page<ProductDTO> getProductsByCategory(@RequestParam(required = false) Long parentCategoryId, 
	                                               @RequestParam(required = false) Long categoryId,
	                                               @RequestParam(required = false) String search, // Tìm kiếm theo tên sản phẩm
	                                               @RequestParam(defaultValue = "0") int page, // Default to page 0
	                                               @RequestParam(defaultValue = "12") int size) { // Default to 12 products per page
	    Page<Product> products;

	    Pageable pageable = PageRequest.of(page, size); // Create Pageable instance

	    if (parentCategoryId != null && search != null && !search.isEmpty()) {
	        // Tìm sản phẩm theo category cha và từ khóa tìm kiếm
	        products = productRepository.findByParentCategoryIdAndNameContainingAndDeletedFalse(parentCategoryId, search, pageable);
	    } else if (categoryId != null && search != null && !search.isEmpty()) {
	        // Tìm sản phẩm theo category con và từ khóa tìm kiếm
	        products = productRepository.findByCategoryIdAndNameContainingAndDeletedFalse(categoryId, search, pageable);
	    } else if (parentCategoryId != null) {
	        // Tìm sản phẩm theo category cha mà không cần tìm kiếm
	        products = productRepository.findByParentCategoryIdAndDeletedFalse(parentCategoryId, pageable);
	    } else if (categoryId != null) {
	        // Tìm sản phẩm theo category con mà không cần tìm kiếm
	        products = productRepository.findByCategoryIdAndDeletedFalse(categoryId, pageable);
	    } else {
	        // Nếu không có categoryId hoặc parentCategoryId, tìm tất cả sản phẩm
	        products = productRepository.findAllByNameContainingAndDeletedFalse(search, pageable);
	    }

	    // Chuyển đổi từ Product sang ProductDTO
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
	        productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
	        productDTO.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
	        return productDTO;
	    });

	    return productDTOs; // Trả về kết quả đã phân trang
	}

}
