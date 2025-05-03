package com.example.demo.repository;

import com.example.demo.modal.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findByDeletedFalse(Pageable pageable);
	@Query("SELECT p FROM Product p WHERE DATE(p.createdDate) = CURRENT_DATE AND p.deleted = false")
	List<Product> findProductsCreatedTodayNotDeleted();
	@Query("SELECT p FROM Product p WHERE (p.category.id = :categoryId OR p.parentCategory.id = :parentCategoryId) AND p.id != :productId AND p.deleted = false")
	List<Product> findByCategoryIdOrParentCategoryIdAndIdNot(Long categoryId, Long parentCategoryId, Long productId);
	@Query("SELECT p FROM Product p WHERE (p.category.id = :categoryId OR p.parentCategory.id = :parentCategoryId) AND p.deleted = false")
	List<Product> findByCategoryIdOrParentCategoryId(Long categoryId, Long parentCategoryId);
	 int countByCategoryId(Long categoryId);
	 Page<Product> findByParentCategoryIdAndDeletedFalse(Long parentCategoryId, Pageable pageable);
	  Page<Product> findByParentCategoryIdAndNameContainingAndDeletedFalse(Long parentCategoryId, String search, Pageable pageable);

	    // Tìm sản phẩm theo categoryId và từ khóa tìm kiếm (name)
	    Page<Product> findByCategoryIdAndNameContainingAndDeletedFalse(Long categoryId, String search, Pageable pageable);
	    Page<Product> findAllByNameContainingAndDeletedFalse(String search, Pageable pageable);
	  Page<Product> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);
	  
	  @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.category.id = :categoryId")
	  List<Product> findTodayProductsByCategory(@Param("categoryId") Long categoryId);
}
