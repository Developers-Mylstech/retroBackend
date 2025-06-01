package com.mylstech.rentro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String clientSecret;
    private String status;
    private Double totalAmount;
    private boolean success;
    private String message;
}