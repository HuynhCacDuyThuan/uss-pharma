package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Message;
import com.example.demo.modal.Product;
import com.example.demo.modal.ProductImage;
import com.example.demo.modal.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;



@Controller
public class ProductDetailController {
	@Autowired
    private ProductRepository productRepository;
	@Autowired
	 UserRepository userRepository;
	 @Autowired
	    private MessageRepository messageRepository;
	@GetMapping("/chi-tiet-san-pham/{id}")
    public String getProductById(@PathVariable Long id, Model model  , @AuthenticationPrincipal UserDetails userDetails) {
        // Find the product by ID
        Optional<Product> optionalProduct = productRepository.findById(id);
       
        // If the product is found, convert it to a DTO and return the view
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
            productDTO.setImageUrl(product.getImageUrl());
            // Set category and parentCategory
            productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
            productDTO.setParentCategoryId(product.getParentCategory() != null ? product.getParentCategory().getId() : null);
            List<String> imageUrls = product.getProductImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            productDTO.setProductImageUrls(imageUrls);
            // Convert the list of similar products to ProductDTOs
            List<Product> similarProducts = productRepository.findByCategoryIdOrParentCategoryIdAndIdNot(
                    product.getCategory() != null ? product.getCategory().getId() : null,
                    product.getParentCategory() != null ? product.getParentCategory().getId() : null,
                    product.getId()  // Exclude the current product
            );

            // Convert the list of similar products to ProductDTOs
            List<ProductDTO> similarProductDTOs = similarProducts.stream()
                    .map(this::convertToDTO)  // Convert each product to DTO
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
            // If product not found, you can redirect to a different view or return an error page
            return "/";
        }
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
}
