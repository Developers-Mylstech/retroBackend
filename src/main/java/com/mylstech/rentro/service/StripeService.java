package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.response.PaymentResponse;
import com.mylstech.rentro.dto.request.StripePaymentRequest;

public interface StripeService {
    PaymentResponse createPaymentIntent(StripePaymentRequest request);
    PaymentResponse confirmPayment(String paymentIntentId);
}