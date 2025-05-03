package com.example.demo.modal;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Parent category (refers to ParentCategory entity)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ParentCategory parentCategory;  // This now references the ParentCategory entity

    // Products in this category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
    @Column(nullable = false)
    private boolean deleted = false;  // Default value is false
    @Column(nullable = true)
    private String imageUrl;  // Field for storing category image URL
}