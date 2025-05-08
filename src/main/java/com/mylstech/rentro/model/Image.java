package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Many-to-many relationship with Product
    @ManyToMany(mappedBy = "images")
    private List<Product> products = new ArrayList<>();
    
    // Helper method to extract file path from URL for deletion
    public String getFilePath() {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
    
    // Helper method to add a product
    public void addProduct(Product product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        
        if (!this.products.contains(product)) {
            this.products.add(product);
        }
    }
    
    // Helper method to remove a product
    public void removeProduct(Product product) {
        if (this.products != null) {
            this.products.remove(product);
        }
    }
}
