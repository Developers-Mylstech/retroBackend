package com.mylstech.rentro.model;

import com.mylstech.rentro.util.PAYMENT_OPTION;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkoutId;
    @ManyToOne
    private Cart cart;
    private String name;
    private String mobile;
    private String email;
    private String homeAddress;
    @Enumerated(EnumType.STRING)
    private PAYMENT_OPTION paymentOption;
}
