package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.PaymentResponse;
import com.mylstech.rentro.dto.StripePaymentRequest;

public interface StripeService {
    PaymentResponse createPaymentIntent(StripePaymentRequest request);
    PaymentResponse confirmPayment(String paymentIntentId);
}