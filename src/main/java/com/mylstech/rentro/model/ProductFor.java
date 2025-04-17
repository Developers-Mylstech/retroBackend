package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product_for")
public class ProductFor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productForId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Sell sell;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Rent rent;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RequestQuotation requestQuotation;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Service services;
}
