package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ParentCategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.Message;
import com.example.demo.modal.ParentCategory;
import com.example.demo.modal.Product;
import com.example.demo.modal.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ParentCategoryService;

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
	@GetMapping("/gioi-thieu")
	public String getAboutPage(Model model , @AuthenticationPrincipal UserDetails userDetails) {
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
		return "user/about"; // Assuming the about page is named 'about.html'
	}

	@GetMapping("/lien-he")
	public String getContact(Model model , @AuthenticationPrincipal UserDetails userDetails) {
		

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

	

	@GetMapping("/san-pham")
	public String getProductByCategoryName(@RequestParam(value = "categoryIds", required = false) List<Long> categoryIds, Model model  , @AuthenticationPrincipal UserDetails userDetails) {
	    List<ParentCategoryDTO> parentCategories = getAllParentCategories();
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

	private List<ProductDTO> fetchProductsForCategory(ParentCategoryDTO parentCategory, List<CategoryDTO> childCategories) {
	    List<Product> products = productRepository.findByCategoryIdOrParentCategoryId(parentCategory.getId(), parentCategory.getId());
	    return products.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private List<ProductDTO> fetchProductsForChildCategory(CategoryDTO childCategory) {
	    List<Product> products = productRepository.findByCategoryIdOrParentCategoryId(childCategory.getId(), childCategory.getParentCategoryId());
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
	    productDTO.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
	    return productDTO;
	}


	public List<ParentCategoryDTO> getAllParentCategories() {
		// Fetch parent categories and convert them to ParentCategoryDTO
		List<ParentCategory> parentCategories = parentCategoryRepository.findAll();

		return parentCategories.stream().map(category -> new ParentCategoryDTO(category.getId(), category.getName()))
				.collect(Collectors.toList());
	}
}
