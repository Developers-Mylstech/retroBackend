package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    @ManyToOne
    private AppUser user;
    @OneToMany(cascade = CascadeType.REMOVE ,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CartItem> items;
    private Double totalPrice;

}
