package com.mylstech.rentro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "request_quotations")

public class RequestQuotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestQuotationId;
    private Double actualPrice;
    private Double discountPrice;
    private Double vat;

}
