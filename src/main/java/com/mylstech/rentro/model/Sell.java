package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @ElementCollection
    @CollectionTable(name = "sell_benefits",
            joinColumns = @JoinColumn(name = "sell_id"))
    @Column(name = "benefit")
    private List<String> benefits;
}
