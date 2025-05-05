package com.mylstech.rentro.model;

import com.mylstech.rentro.util.PAYMENT_OPTION;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "check_out")
public class CheckOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkoutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_cart_id", nullable = false)
    private Cart cart;

    private String firstName;
    private String lastName;

    private String mobile;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;

    private String homeAddress;


    @Enumerated(EnumType.STRING)
    private PAYMENT_OPTION paymentOption;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now ( );
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now ( );
    }
}