package com.mylstech.rentro.model;

import com.mylstech.rentro.util.ProductType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private String productName;
    
    private String productImage;
    
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    
    private Integer quantity;
    
    private Integer rentPeriod;
    
    private Double price;
    
    private Double totalPrice;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        
        // Calculate total price
        if (totalPrice == null) {
            totalPrice = price * quantity;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Recalculate total price on update
        totalPrice = price * quantity;
    }
}