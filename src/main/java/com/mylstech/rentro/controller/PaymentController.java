package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.PaymentResponse;
import com.mylstech.rentro.dto.StripePaymentRequest;
import com.mylstech.rentro.service.OrderService;
import com.mylstech.rentro.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/create-payment-intent")
    @Operation(summary = "Create a payment intent with Stripe")
    public ResponseEntity<PaymentResponse> createPaymentIntent(@RequestBody StripePaymentRequest request) {
        logger.debug("Creating payment intent for order: {}", request.getOrderId());
        PaymentResponse response = stripeService.createPaymentIntent(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm/{paymentIntentId}")
    @Operation(summary = "Confirm a payment with Stripe")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @PathVariable String paymentIntentId,
            @RequestParam Long orderId) {
        logger.debug("Confirming payment for order: {} with payment intent: {}", orderId, paymentIntentId);
        
        PaymentResponse response = stripeService.confirmPayment(paymentIntentId);
        
        if (response.isSuccess() && "succeeded".equals(response.getStatus())) {
            // Mark order as paid
            orderService.markOrderAsPaid(orderId, paymentIntentId);
        }
        
        return ResponseEntity.ok(response);
    }
}