package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ParentCategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.modal.Category;
import com.example.demo.modal.ParentCategory;
import com.example.demo.modal.Product;
import com.example.demo.modal.ProductImage;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ParentCategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ParentCategoryService;
import com.example.demo.service.ProductService;


import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/products")
public class ProductController {

    @Value("${upload.path}")
    private String pathUploadImage;

    @Autowired
    private ProductService productService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParentCategoryService parentCategoryService;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/add")
    public String showAddProductForm(HttpServletRequest request,Model model) {
        model.addAttribute("product", new ProductDTO());

        // Get parent categories
        List<ParentCategoryDTO> parentCategories = getAllParentCategories();
        Map<Long, List<CategoryDTO>> childCategoriesMap = new HashMap<>();

        // Fetch child categories for each parent
        for (ParentCategoryDTO parentCategory : parentCategories) {
            List<CategoryDTO> childCategories = categoryService.getCategoriesByParentId(parentCategory.getId());
            childCategoriesMap.put(parentCategory.getId(), childCategories);
        }
        String baseUrl = getBaseUrl(request);
        
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategoriesMap", childCategoriesMap);

        return "admin/add-product";  // Return to the product add form page
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

//		 if ((scheme.equals("http") && !"80".equals(port) && !"8080".equals(port))
//		            || (scheme.equals("https") && !"443".equals(port))) {
//		        baseUrl += ":" + port;
//		    }


		return baseUrl;
	}
    @PostMapping("/add")
    public String addProduct(
        @ModelAttribute("product") ProductDTO productDTO, 
        ModelMap model, 
        @RequestParam("file") MultipartFile file,  // Main image
        @RequestParam("files") List<MultipartFile> files,  // Additional images
        HttpServletRequest httpServletRequest) throws IOException {

        
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setSubDescription(productDTO.getSubDescription());
        product.setDiscount(productDTO.getDiscount());
        product.setCreatedDate(new Date());  // Automatically set the creation date
        product.setProductCode(productDTO.getProductCode());
        product.setBrand(productDTO.getBrand());
        productDTO.setCurrency("VND");
        product.setValidUntil(productDTO.getValidUntil());
        product.setRating(productDTO.getRating());
        product.setSlug(productDTO.getSlug());
        product.setTitle(productDTO.getTitle());
        File uploadDir = new File(pathUploadImage);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();  // Create the directory if it doesn't exist
        }

    
        String mainImageFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();  // Ensure unique filename
        File mainImageFile = new File(uploadDir, mainImageFileName);
        try (FileOutputStream fos = new FileOutputStream(mainImageFile)) {
            fos.write(file.getBytes());
        }
        product.setImageUrl(mainImageFileName);  // Set the main image URL to match 'main_image' in the database
        if (productDTO.getParentCategoryId() != null) {
            ParentCategory parentCategory = parentCategoryRepository.findById(productDTO.getParentCategoryId())
                    .orElse(null);  
            if (parentCategory != null) {
                product.setParentCategory(parentCategory);  
            }
        }

       
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElse(null);  
            if (category != null) {
                product.setCategory(category);  
            }
        }
    
        productRepository.save(product);

        for (MultipartFile multipartFile : files) {
            if (!multipartFile.isEmpty()) {  
                String additionalImageFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();  // Đảm bảo tên file duy nhất
                File imageFile = new File(uploadDir, additionalImageFileName);
                try (FileOutputStream imageFos = new FileOutputStream(imageFile)) {
                    imageFos.write(multipartFile.getBytes());
                }

                // Lưu ảnh phụ vào cơ sở dữ liệu
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(additionalImageFileName);
                productImage.setProduct(product);  
                productImageRepository.save(productImage);  
            }
        }
        // Add success message and redirect
        model.addAttribute("message", "Product added successfully!");
        model.addAttribute("product", product);

        return "redirect:/quan-ly-san-pham"; 
    }
    @GetMapping("/api/edit/{id}")
    public ResponseEntity<?> showEditProductForm(@PathVariable("id") Long id,Model model) {
        // Fetch the product by its ID
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // Get parent categories
        List<ParentCategoryDTO> parentCategories = getAllParentCategories();
        Map<Long, List<CategoryDTO>> childCategoriesMap = new HashMap<>();

        // Fetch child categories for each parent
        for (ParentCategoryDTO parentCategory : parentCategories) {
            List<CategoryDTO> childCategories = categoryService.getCategoriesByParentId(parentCategory.getId());
            childCategoriesMap.put(parentCategory.getId(), childCategories);
        }

        // Prepare response data for ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setSubDescription(product.getSubDescription());
        productDTO.setDiscount(product.getDiscount());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setParentCategoryId(product.getCategory().getParentCategory().getId());
        productDTO.setCreatedDate(new Date());
        // Set product images (if any)
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add(product.getImageUrl()); // You can add more image URLs if needed
        productDTO.setProductImageUrls(imageUrls);

        productDTO.setProductCode(product.getProductCode());
        productDTO.setBrand(product.getBrand());
        productDTO.setCurrency("VND");
        productDTO.setValidUntil(product.getValidUntil());
        productDTO.setRating(product.getRating());
        productDTO.setSlug(product.getSlug());
     
        Map<String, Object> response = new HashMap<>();
        response.put("product", productDTO); // The product data
        response.put("parentCategories", parentCategories); // Parent categories
        response.put("childCategoriesMap", childCategoriesMap); // Child categories map

     
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
     
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

    
        List<ParentCategoryDTO> parentCategories = getAllParentCategories();
        Map<Long, List<CategoryDTO>> childCategoriesMap = new HashMap<>();

       
        for (ParentCategoryDTO parentCategory : parentCategories) {
            List<CategoryDTO> childCategories = categoryService.getCategoriesByParentId(parentCategory.getId());
            childCategoriesMap.put(parentCategory.getId(), childCategories);
        }

        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategoriesMap", childCategoriesMap);
        String baseUrl = getBaseUrl(request);
        model.addAttribute("baseUrl", baseUrl);
        // Directly add the product entity to the model
        model.addAttribute("product", product);
    
        return "admin/edit-product";
    }
    
    @PostMapping("/edit/{id}")
    public String editProduct(
        @PathVariable("id") Long id,
        @ModelAttribute("product") ProductDTO productDTO,
        @RequestParam("file") MultipartFile file,
        @RequestParam("files") List<MultipartFile> files,
        Model model) throws IOException {

        // Fetch the existing product
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // Get the upload directory
        File uploadDir = new File(pathUploadImage);  // Initialize the upload directory

        // Ensure the upload directory exists or create it if it doesn't
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();  // Create the directory if it doesn't exist
        }

        // Update product information
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDiscount(productDTO.getDiscount());
        product.setDescription(productDTO.getDescription());
        product.setSubDescription(productDTO.getSubDescription());
        product.setProductCode(productDTO.getProductCode());
        product.setBrand(productDTO.getBrand());
        productDTO.setCurrency("VND");
        product.setValidUntil(productDTO.getValidUntil());
        product.setRating(productDTO.getRating());
        product.setSlug(productDTO.getSlug());
        product.setTitle(productDTO.getTitle());
        // Handle main image upload
        if (file != null && !file.isEmpty()) {
            // Handle main image upload
            String mainImageFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();  // Ensure unique filename
            File mainImageFile = new File(uploadDir, mainImageFileName);
            try (FileOutputStream fos = new FileOutputStream(mainImageFile)) {
                fos.write(file.getBytes());
            }
            product.setImageUrl(mainImageFileName);  // Set the main image URL to match 'main_image' in the database
        }
        else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
      
            product.setImageUrl(product.getImageUrl());  // Keep the old image URL
        }

     
        if (files != null && !files.isEmpty()) {
         
            List<ProductImage> existingImages = productImageRepository.findByProduct(product);

      
            List<String> uploadedImageNames = new ArrayList<>();

            
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.isEmpty()) {  // Check if the file is not empty
                 
                    String additionalImageFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
                    uploadedImageNames.add(additionalImageFileName);

                
                    File imageFile = new File(uploadDir, additionalImageFileName);
                    try (FileOutputStream imageFos = new FileOutputStream(imageFile)) {
                        imageFos.write(multipartFile.getBytes());
                    }

                    // Save the new image to the database
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(additionalImageFileName);
                    productImage.setProduct(product);  // Link the image to the product
                    productImageRepository.save(productImage);  // Save the image to the database
                }
            }

          
        }

        // If no new images are uploaded, we do nothing with the existing images:
        // The old images remain in the database and file system.
        else if (files == null || files.isEmpty()) {
            List<ProductImage> existingImages = productImageRepository.findByProduct(product);

            // Log or confirm that old images remain intact
            if (existingImages != null && !existingImages.isEmpty()) {
                System.out.println("Existing images are retained: " + existingImages.size() + " image(s) remain.");
            } else {
                System.out.println("No existing images found to retain.");
            }
        }

        // Set the category and parent category if IDs are provided
        if (productDTO.getParentCategoryId() != null) {
            ParentCategory parentCategory = parentCategoryRepository.findById(productDTO.getParentCategoryId())
                    .orElse(null);  // Return null if not found
            if (parentCategory != null) {
                product.setParentCategory(parentCategory);  // Set the parent category
            }
        }

        // Set the category (sub-category) if the ID is provided
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElse(null);  // Return null if not found
            if (category != null) {
                product.setCategory(category);  // Set the category
            }
        }

        // Save updated product
        productRepository.save(product);
        model.addAttribute("message", "Product updated successfully!");

        return "redirect:/quan-ly-san-pham"; 
    }


    public List<ParentCategoryDTO> getAllParentCategories() {
        List<ParentCategory> parentCategories = parentCategoryRepository.findAll();
        return parentCategories.stream()
                .map(category -> new ParentCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
    
    @PostMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // Mark the product as deleted (soft delete)
        product.setDeleted(true);

        // Save the updated product to the database
        productRepository.save(product);

        // Return success response
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{subCategoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long subCategoryId, @RequestBody CategoryDTO subCategoryData) {
        // Fetch the existing category by ID
        Category category = categoryRepository.findById(subCategoryId)
                .orElseThrow(null);

        // Update the fields of the category entity with data from the DTO
        category.setName(subCategoryData.getName());
        

        // Save the updated category
        Category updatedCategory = categoryRepository.save(category);

        // Map the updated category to CategoryDTO and return the response
        CategoryDTO categoryDTO = new CategoryDTO(updatedCategory.getId(), updatedCategory.getName(),
                                                  updatedCategory.getParentCategory() != null ? updatedCategory.getParentCategory().getId() : null);

        return ResponseEntity.ok(categoryDTO);
    }
}
