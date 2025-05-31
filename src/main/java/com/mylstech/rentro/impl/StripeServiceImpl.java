package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.PaymentResponse;
import com.mylstech.rentro.dto.StripePaymentRequest;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Cart;
import com.mylstech.rentro.model.CheckOut;
import com.mylstech.rentro.repository.CheckOutRepository;
import com.mylstech.rentro.service.CartService;
import com.mylstech.rentro.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {

    private final CheckOutRepository checkOutRepository;
    private final CartService cartService;

    @Override
    public PaymentResponse createPaymentIntent(StripePaymentRequest request) {
        try {
            CheckOut checkOut = checkOutRepository.findById ( request.getCheckoutId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Checkout not found with id: " + request.getCheckoutId ( ) ) );
            log.info ("total price:" +checkOut.getCart ( ).getTotalPrice ( ) );
            // Convert amount to cents (Stripe uses smallest currency unit)
            long amountInCents = Math.round ( checkOut.getCart ( ).getTotalPrice ( ) * 100 );
            log.info ( "Amount in cents: " + amountInCents );
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder ( )
                    .setAmount ( amountInCents )
                    .setCurrency ( "AED" )
                    .setAutomaticPaymentMethods (
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder ( )
                                    .setEnabled ( true )
                                    .build ( )
                    )
                    .putMetadata ( "checkout", request.getCheckoutId ( ).toString ( ) )
                    .build ( );

            PaymentIntent paymentIntent = PaymentIntent.create ( params );
            PaymentResponse paymentIntentCreatedSuccessfully = new PaymentResponse (
                    paymentIntent.getId ( ),
                    paymentIntent.getClientSecret ( ),
                    paymentIntent.getStatus ( ),
                    paymentIntent.getAmount ( ).doubleValue ( ),
                    true,
                    "Payment intent created successfully"
            );
            log.info ( "Payment intent created successfully: {}", paymentIntentCreatedSuccessfully);
            return paymentIntentCreatedSuccessfully;
        }
        catch ( StripeException e ) {
            log.error ( "Error creating payment intent", e );
            return new PaymentResponse (
                    null,
                    null,
                    "failed",
                    0.0,
                    false,
                    e.getMessage ( )
            );
        }
    }

    @Override
    public PaymentResponse confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve ( paymentIntentId );
            if ( "succeeded".equals ( paymentIntent.getStatus ( ) ) ) {

                String checkout = paymentIntent.getMetadata ( ).get ( "checkout" );
                Long checkoutId = Long.parseLong ( checkout );
                log.info( "Clearing cart for checkout: {}", checkoutId );
                CheckOut checkOut = checkOutRepository.findById ( checkoutId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( "Checkout not found with id: " + checkoutId ) );
                Cart cart = checkOut.getCart ( );
                log.info ( "is Cart temporary: {}", cart.isTemporary () );
                if ( ! cart.isTemporary ( ) ) {
                    log.info ( "------------------------------------->Clearing cart");
                    cartService.clearCart ( );
                    log.info ( "----------------------------------->Cart cleared");
                }
            }
            return new PaymentResponse (
                    paymentIntent.getId ( ),
                    paymentIntent.getClientSecret ( ),
                    paymentIntent.getStatus ( ),
                    paymentIntent.getAmount ( ).doubleValue ( ),
                    "succeeded".equals ( paymentIntent.getStatus ( ) ),
                    "Payment status retrieved"
            );
        }
        catch ( StripeException e ) {
            log.error ( "Error confirming payment", e );
            return new PaymentResponse (
                    e.getRequestId ( ),
                    null,
                    "failed",
                    0.0,
                    false,
                    e.getMessage ( )
            );
        }
    }
}