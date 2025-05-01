package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartResponse;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.Cart;
import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.AppUserRepository;
import com.mylstech.rentro.repository.CartItemRepository;
import com.mylstech.rentro.repository.CartRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.security.AppUserSecurityDetails;
import com.mylstech.rentro.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public CartResponse getCurrentUserCart() {
        AppUser currentUser = getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );
        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(CartItemRequest request) {
        if ( ! request.isValid ( ) ) {
            throw new IllegalArgumentException ( "Invalid cart item request" );
        }

        AppUser currentUser = getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        // Find the product
        Product product = productRepository.findById ( request.getProductId ( ) )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + request.getProductId ( ) ) );

        // Create the cart item
        CartItem cartItem = request.toCartItem ( product );
        cartItem = cartItemRepository.save ( cartItem );

        // Add to cart
        cart.getItems ( ).add ( cartItem );
        cart = cartRepository.save ( cart );

        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse addItemsToCart(List<CartItemRequest> requests) {
        if ( requests == null || requests.isEmpty ( ) ) {
            throw new IllegalArgumentException ( "Cart item requests cannot be empty" );
        }

        // Validate all requests
        for (CartItemRequest request : requests) {
            if ( ! request.isValid ( ) ) {
                throw new IllegalArgumentException ( "Invalid cart item request" );
            }
        }

        AppUser currentUser = getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        // Process each request
        for (CartItemRequest request : requests) {
            // Find the product
            Product product = productRepository.findById ( request.getProductId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + request.getProductId ( ) ) );

            // Create the cart item
            CartItem cartItem = request.toCartItem ( product );
            cartItem = cartItemRepository.save ( cartItem );

            // Add to cart
            cart.getItems ( ).add ( cartItem );
        }

        // Save the cart once with all new items
        cart = cartRepository.save ( cart );

        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long cartItemId) {
        AppUser currentUser = getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        cart.getItems ( ).removeIf ( item -> item.getCartItemId ( ).equals ( cartItemId ) );
        cart = cartRepository.save ( cart );

        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse clearCart() {
        AppUser currentUser = getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        cart.getItems ( ).clear ( );
        cart = cartRepository.save ( cart );

        return new CartResponse ( cart );
    }

    private AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext ( ).getAuthentication ( );
        if ( authentication == null || ! authentication.isAuthenticated ( ) ) {
            throw new RuntimeException ( "User not authenticated" );
        }

        Object principal = authentication.getPrincipal ( );

        // Check what type of principal we have
        if ( principal instanceof AppUserSecurityDetails ) {
            // If principal is AppUserSecurityDetails, get the wrapped AppUser
            AppUserSecurityDetails userDetails = (AppUserSecurityDetails) principal;
            // We need to access the user field, but it's private and final
            // Let's use the email to find the user
            String email = userDetails.getUsername ( ); // This returns user.getEmail()
            return appUserRepository.findByEmail ( email )
                    .orElseThrow ( () -> new RuntimeException ( "User not found with email: " + email ) );
        } else if ( principal instanceof String ) {
            // If principal is a String (likely username/email), find the user
            String email = (String) principal;
            return appUserRepository.findByEmail ( email )
                    .orElseThrow ( () -> new RuntimeException ( "User not found with email: " + email ) );
        } else {
            // Handle other cases based on your security configuration
            throw new RuntimeException ( "Unexpected principal type: " + principal.getClass ( ).getName ( ) );
        }
    }

    private Cart getOrCreateCart(AppUser user) {
        return cartRepository.findByUser ( user )
                .orElseGet ( () -> {
                    Cart newCart = new Cart ( );
                    newCart.setUser ( user );
                    newCart.setItems ( new ArrayList<> ( ) );
                    return cartRepository.save ( newCart );
                } );
    }
}
