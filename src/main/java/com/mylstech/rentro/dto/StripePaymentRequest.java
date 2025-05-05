package com.mylstech.rentro.dto;

import lombok.Data;

@Data
public class StripePaymentRequest {
    private Long orderId;
    private Double amount;
    private String currency;
    private String description;
}