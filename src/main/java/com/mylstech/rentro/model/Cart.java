package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    private Double totalPrice = 0.0;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Flag to mark temporary carts created for buy now functionality
    private boolean temporary = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculate total price based on cart items
     */
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getPrice() != null ? item.getPrice() : 0.0)
                .sum();
    }
    
    /**
     * Helper method to add an item to the cart
     * @param item the item to add
     */
    public void addItem(CartItem item) {
        item.setCart(this);
        items.add(item);
    }
    
    /**
     * Helper method to remove an item from the cart
     * @param item the item to remove
     */
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
}
