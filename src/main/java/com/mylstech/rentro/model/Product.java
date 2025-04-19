package com.mylstech.rentro.model;

import jakarta.persistence.*;
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
    private String longDescription;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Brand brand;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductImages productImages;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Specification> specification;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductFor productFor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Category subCategory;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Inventory inventory;

    @ElementCollection
    @CollectionTable(name = "key_features",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "key_feature")
    private List<String> keyFeatures;

    private String manufacturer;
}
