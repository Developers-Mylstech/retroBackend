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
@Table(name = "productImages")
public class ProductImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productImageId;
    @ManyToOne(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @ElementCollection
    @CollectionTable(name = "product_image_urls",
            joinColumns = @JoinColumn(name = "product_image_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;



}
