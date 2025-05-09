package com.mylstech.rentro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // Many-to-many relationship with Image
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_images",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images = new ArrayList<>();
    
    // Keep the old field for backward compatibility during migration
    @ElementCollection
    @CollectionTable(name = "product_image_urls",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Access(AccessType.PROPERTY)
    @Deprecated
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_services",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<OurService> ourServices = new ArrayList<>();

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

    // Helper methods for backward compatibility
    public List<String> getImageUrls() {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        return images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());
    }

    public void setImageUrls(List<String> urls) {
        if (urls == null) {
            return;
        }
        
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        
        // Convert URLs to Image entities
        for (String url : urls) {
            // Check if we already have this URL to avoid duplicates
            boolean urlExists = this.images.stream()
                    .anyMatch(img -> img.getImageUrl().equals(url));
                    
            if (!urlExists) {
                Image image = new Image();
                image.setImageUrl(url);
                image.addProduct(this); // Add this product to the image
                this.images.add(image);
            }
        }
    }

    // Helper method to add an image
    public void addImage(Image image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        
        // Check if we already have this image to avoid duplicates
        boolean imageExists = this.images.stream()
                .anyMatch(img -> img.getImageId() != null && 
                                 img.getImageId().equals(image.getImageId()));
                                 
        if (!imageExists) {
            this.images.add(image);
            image.addProduct(this); // Add this product to the image
        }
    }
    
    // Helper method to remove an image
    public void removeImage(Image image) {
        if (this.images != null) {
            this.images.remove(image);
            // Remove this product from the image's products list
            if (image.getProducts() != null) {
                image.getProducts().remove(this);
            }
        }
    }

    // Helper method to add a service
    public void addOurService(OurService ourService) {
        if (this.ourServices == null) {
            this.ourServices = new ArrayList<>();
        }
        
        // Check if we already have this service to avoid duplicates
        boolean serviceExists = this.ourServices.stream()
                .anyMatch(svc -> svc.getOurServiceId() != null && 
                                 svc.getOurServiceId().equals(ourService.getOurServiceId()));
                                 
        if (!serviceExists) {
            this.ourServices.add(ourService);
            // Add this product to the service's products list
            if (ourService.getProducts() != null) {
                if (!ourService.getProducts().contains(this)) {
                    ourService.getProducts().add(this);
                }
            } else {
                List<Product> products = new ArrayList<>();
                products.add(this);
                ourService.setProducts(products);
            }
        }
    }

    // Helper method to remove a service
    public void removeOurService(OurService ourService) {
        if (this.ourServices != null) {
            this.ourServices.remove(ourService);
            // Remove this product from the service's products list
            if (ourService.getProducts() != null) {
                ourService.getProducts().remove(this);
            }
        }
    }
}
