package com.mylstech.rentro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private String name;
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Category> subCategories;

    // On parentCategory (ManyToOne side)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Category parentCategory;

    @ElementCollection
    @CollectionTable(name = "category_image_urls",
            joinColumns = @JoinColumn(name = "category_image_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

}
