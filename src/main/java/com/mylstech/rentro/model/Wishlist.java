package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlists")
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @ManyToMany
    @JoinTable(
        name = "wishlist_products",
        joinColumns = @JoinColumn(name = "wishlist_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * Add a product to the wishlist
     * @param product The product to add
     * @return true if the product was added, false if it was already in the wishlist
     */
    public boolean addProduct(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        
        // Check if product is already in wishlist
        if (products.contains(product)) {
            return false;
        }
        
        return products.add(product);
    }
    
    /**
     * Remove a product from the wishlist
     * @param product The product to remove
     * @return true if the product was removed, false if it wasn't in the wishlist
     */
    public boolean removeProduct(Product product) {
        if (products == null) {
            return false;
        }
        
        return products.remove(product);
    }
    
    /**
     * Check if a product is in the wishlist
     * @param productId The ID of the product to check
     * @return true if the product is in the wishlist, false otherwise
     */
    public boolean containsProduct(Long productId) {
        if (products == null) {
            return false;
        }
        
        return products.stream()
                .anyMatch(p -> p.getProductId().equals(productId));
    }
}
