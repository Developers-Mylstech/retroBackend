package com.mylstech.rentro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OurService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ourServiceId;
    private String title;
    private String shortDescription;
    private String detailedHeading;
    @Size(max = 500)
    private String detailedDescription;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "our_service_id")
    private List<Image> images = new ArrayList<>();
//    @ElementCollection
//    @CollectionTable(name = "our_services_image_urls",
//            joinColumns = @JoinColumn(name = "our_service_id"))
//    @Column(name = "image_url")
//    @Deprecated
//    private List<String> imageUrl;
    private String imageUrl;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feature> feature;

    @ManyToMany(mappedBy = "ourServices")
    private List<Product> products = new ArrayList<>();

    // Helper method to add a product
    public void addProduct(Product product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        
        // Check if we already have this product to avoid duplicates
        boolean productExists = this.products.stream()
                .anyMatch(p -> p.getProductId() != null && 
                              p.getProductId().equals(product.getProductId()));
                              
        if (!productExists) {
            this.products.add(product);
            // Add this service to the product's services list
            if (product.getOurServices() != null) {
                if (!product.getOurServices().contains(this)) {
                    product.getOurServices().add(this);
                }
            } else {
                List<OurService> services = new ArrayList<>();
                services.add(this);
                product.setOurServices(services);
            }
        }
    }

    // Helper method to remove a product
    public void removeProduct(Product product) {
        if (this.products != null) {
            this.products.remove(product);
            // Remove this service from the product's services list
            if (product.getOurServices() != null) {
                product.getOurServices().remove(this);
            }
        }
    }
}
