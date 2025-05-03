package com.example.demo.repository;

import com.example.demo.modal.Product;
import com.example.demo.modal.ProductImage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
	  // Custom query to find ProductImages associated with a specific Product
    List<ProductImage> findByProduct(Product product);
}
