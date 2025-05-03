package com.example.demo.dto;
public class CategoryDTO {
    private Long id;
    private String name;
    private int productCount;
	private Long parentCategoryId; // Only store the ID of the parent to prevent recursion
    private String imageUrl; // Add the imageUrl field
    // Constructor

	public CategoryDTO(Long id, String name, Long parentCategoryId) {
		this.id = id;
		this.name = name;
		this.parentCategoryId = parentCategoryId;
	}
    public CategoryDTO() {}

	public CategoryDTO(Long id, Long parentCategoryId) {
		super();
		this.id = id;
		this.parentCategoryId = parentCategoryId;
	}

	public CategoryDTO(Long id, String name, int productCount, String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.productCount = productCount;
		this.imageUrl = imageUrl;
	}
	public CategoryDTO(Long id, String name, String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
	}
	public CategoryDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	
	
}
