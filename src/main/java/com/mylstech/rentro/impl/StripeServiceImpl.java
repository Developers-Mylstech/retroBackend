package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.PaymentResponse;
import com.mylstech.rentro.dto.StripePaymentRequest;
import com.mylstech.rentro.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    
    private final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);
    
    @Override
    public PaymentResponse createPaymentIntent(StripePaymentRequest request) {
        try {
            // Convert amount to cents (Stripe uses smallest currency unit)
            long amountInCents = Math.round(request.getAmount() * 100);
            
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription(request.getDescription())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("orderId", request.getOrderId().toString())
                    .build();
            
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            return new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret(),
                    paymentIntent.getStatus(),
                    true,
                    "Payment intent created successfully"
            );
        } catch (StripeException e) {
            logger.error("Error creating payment intent", e);
            return new PaymentResponse(
                    null,
                    null,
                    "failed",
                    false,
                    e.getMessage()
            );
        }
    }
    
    @Override
    public PaymentResponse confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            return new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret(),
                    paymentIntent.getStatus(),
                    "succeeded".equals(paymentIntent.getStatus()),
                    "Payment status retrieved"
            );
        } catch (StripeException e) {
            logger.error("Error confirming payment", e);
            return new PaymentResponse(
                    null,
                    null,
                    "failed",
                    false,
                    e.getMessage()
            );
        }
    }
}