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
@Table(name = "rents")

public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentId;
    private Double monthlyPrice;
    private Double discountPrice;
    private Double vat;
    @ElementCollection
    @CollectionTable(name = "rent_benefits",
            joinColumns = @JoinColumn(name = "rent_id"))
    @Column(name = "benefits")
    private List<String> benefits;


}
