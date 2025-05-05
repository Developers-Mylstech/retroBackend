package com.mylstech.rentro.model;

import com.mylstech.rentro.util.ORDER_STATUS;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @OneToOne
    @JoinColumn(name = "checkout_id")
    private CheckOut checkout;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    private Double totalAmount;
    
    @Enumerated(EnumType.STRING)
    private ORDER_STATUS status = ORDER_STATUS.PENDING;
    
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;
    
    private LocalDateTime deliveryDate;
    
    private String paymentId;
    
    private String paymentMethod;
    
    private Boolean isPaid = false;
    
    private LocalDateTime paidAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        
        // Generate order number if not already set
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
        
        // Set default status if not already set
        if (status == null) {
            status = ORDER_STATUS.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculate total amount based on order items
     */
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}