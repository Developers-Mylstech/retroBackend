package com.mylstech.rentro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String name;
    private String description;
    @Size(max = 500)
    private String longDescription;
    private String supplierName;
    private String supplierCode;
    private String modelNo;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Brand brand;

    @ElementCollection
    @CollectionTable(name = "product_image_urls",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Access(AccessType.PROPERTY)
    private List<String> imageUrls;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Specification> specification;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductFor productFor;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Category subCategory;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Inventory inventory;

    @ElementCollection
    @CollectionTable(name = "key_features",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "key_feature")
    @Access(AccessType.PROPERTY)
    private List<String> keyFeatures;

    @ElementCollection
    @CollectionTable(name = "tagNKeywords",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tagNKeywords")
    @Access(AccessType.PROPERTY)
    private List<String> tagNKeywords;

    private String manufacturer;
}
