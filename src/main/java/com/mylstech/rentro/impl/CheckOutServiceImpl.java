package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.CheckOutRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.OrderResponse;
import com.mylstech.rentro.model.Address;
import com.mylstech.rentro.model.Cart;
import com.mylstech.rentro.model.CheckOut;
import com.mylstech.rentro.repository.AddressRepository;
import com.mylstech.rentro.repository.CartRepository;
import com.mylstech.rentro.repository.CheckOutRepository;
import com.mylstech.rentro.service.CartService;
import com.mylstech.rentro.service.CheckOutService;
import com.mylstech.rentro.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckOutServiceImpl implements CheckOutService {
    private final CheckOutRepository checkOutRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderService orderService;
    private final AddressRepository addressRepository;
    private final Logger logger = LoggerFactory.getLogger ( CheckOutServiceImpl.class );

    @Override
    public List<CheckOutResponse> getAllCheckOuts() {
        logger.debug ( "Fetching all checkouts" );
        return checkOutRepository.findAll ( ).stream ( )
                .map ( CheckOutResponse::new )
                .toList ( );
    }

    @Override
    public CheckOutResponse getCheckOutById(Long id) {
        logger.debug ( "Fetching checkout with ID: {}", id );
        CheckOut checkOut = checkOutRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Checkout not found with id: " + id ) );
        return new CheckOutResponse ( checkOut );
    }

    @Override
    @Transactional
    public CheckOutResponse createCheckOut(CheckOutRequest request) {
        logger.debug ( "Creating new checkout: {}", request );


        // Find the cart
        Cart cart = cartRepository.findById ( request.getCartId ( ) )
                .orElseThrow ( () -> new RuntimeException ( "Cart not found with id: " + request.getCartId ( ) ) );

        // Create checkout
        CheckOut checkOut = request.toCheckOut ( cart );

        // Set the delivery address from addressId
        Address address = addressRepository.findById ( request.getAddressId ( ) )
                .orElseThrow ( () -> new RuntimeException ( "Address not found with id: " + request.getAddressId ( ) ) );

        // Verify address belongs to current user
        if ( ! address.getUser ( ).getUserId ( ).equals ( cart.getUser ( ).getUserId ( ) ) ) {
            throw new RuntimeException ( "You don't have permission to use this address" );
        }

        checkOut.setDeliveryAddress ( address );
        // Set homeAddress with formatted address
        checkOut.setHomeAddress ( address.getFormattedAddress ( ) );

        CheckOut savedCheckOut = checkOutRepository.save ( checkOut );
        logger.debug ( "Created checkout with ID: {}", savedCheckOut.getCheckoutId ( ) );
        return placeOrder ( savedCheckOut.getCheckoutId ( ) );
    }


    @Override
    @Transactional
    public CheckOutResponse updateCheckOut(Long id, CheckOutRequest request) {
        logger.debug ( "Updating checkout with ID: {}", id );


        // Find the checkout
        CheckOut checkOut = checkOutRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Checkout not found with id: " + id ) );

        // Find the cart if cart ID is provided
        if ( request.getCartId ( ) != null ) {
            Cart cart = cartRepository.findById ( request.getCartId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Cart not found with id: " + request.getCartId ( ) ) );
            checkOut.setCart ( cart );
        }

        // Update checkout fields
        if ( request.getFirstName ( ) != null ) {
            checkOut.setFirstName ( request.getFirstName ( ) );
        }
        if ( request.getLastName ( ) != null ) {
            checkOut.setLastName ( request.getLastName ( ) );
        }

        if ( request.getMobile ( ) != null ) {
            checkOut.setMobile ( request.getMobile ( ) );
        }

        if ( request.getEmail ( ) != null ) {
            checkOut.setEmail ( request.getEmail ( ) );
        }

        // Update the delivery address if addressId is provided
        if ( request.getAddressId ( ) != null ) {
            Address address = addressRepository.findById ( request.getAddressId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Address not found with id: " + request.getAddressId ( ) ) );

            // Verify address belongs to current user
//            if ( ! address.getUser ( ).getUserId ( ).equals ( checkOut.getCart ( ).getUser ( ).getUserId ( ) ) ) {
//                throw new RuntimeException ( "You don't have permission to use this address" );
//            }

            checkOut.setDeliveryAddress ( address );
            // Update homeAddress with formatted address
            checkOut.setHomeAddress ( address.getFormattedAddress ( ) );
        }


        // Save and return the updated checkout
        CheckOut updatedCheckOut = checkOutRepository.save ( checkOut );
        logger.debug ( "Updated checkout with ID: {}", updatedCheckOut.getCheckoutId ( ) );
        return new CheckOutResponse ( updatedCheckOut );
    }

    @Override
    @Transactional
    public void deleteCheckOut(Long id) {
        logger.debug ( "Deleting checkout with ID: {}", id );

        // Find the checkout
        CheckOut checkOut = checkOutRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Checkout not found with id: " + id ) );

        // Delete the checkout
        checkOutRepository.delete ( checkOut );
        logger.debug ( "Deleted checkout with ID: {}", id );
    }

    @Override
    public List<CheckOutResponse> getCheckOutsByUserId(Long userId) {
        logger.debug ( "Fetching checkouts for user with ID: {}", userId );
        return checkOutRepository.findByUserId ( userId ).stream ( )
                .map ( CheckOutResponse::new )
                .toList ( );
    }





    @Override
    @Transactional
    public CheckOutResponse placeOrder(Long checkoutId) {
        logger.debug ( "Placing order for checkout with ID: {}", checkoutId );

        // Get the checkout
        CheckOut checkout = checkOutRepository.findById ( checkoutId )
                .orElseThrow ( () -> new RuntimeException ( "Checkout not found with id: " + checkoutId ) );


        CheckOut updatedCheckout = checkOutRepository.save ( checkout );

        // Create order from checkout
        OrderResponse orderResponse = orderService.createOrderFromCheckout ( updatedCheckout );

        logger.debug ( "Created order with ID: {} for checkout with ID: {}",
                orderResponse.getOrderId ( ), checkoutId );

        // Check if the cart is temporary (from buy now) and should be deleted
        Cart cart = checkout.getCart ( );
        if ( cart != null && cart.isTemporary ( ) ) {
            logger.debug ( "Detected temporary cart from buy now, will be deleted separately" );
        }
        CheckOutResponse checkOutResponse = new CheckOutResponse ( updatedCheckout );
        checkOutResponse.setOrderId ( orderResponse.getOrderId () );
        return checkOutResponse;
    }
}
