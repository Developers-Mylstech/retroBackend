package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sells")

public class Sell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellId;
    private Double actualPrice;
    private Double discountPrice;
    private Double vat;

}
