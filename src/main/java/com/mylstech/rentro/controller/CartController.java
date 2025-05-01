package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartResponse;
import com.mylstech.rentro.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCurrentUserCart() {
        try {
            CartResponse cart = cartService.getCurrentUserCart();
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            if (!request.isValid()) {
                return ResponseEntity.badRequest().build();
            }
            CartResponse cart = cartService.addItemToCart(request);
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }
    
    @PostMapping("/items/batch")
    public ResponseEntity<CartResponse> addItemsToCart(@Valid @RequestBody List<CartItemRequest> requests) {
        try {
            // Validate all requests
            for (CartItemRequest request : requests) {
                if (!request.isValid()) {
                    return ResponseEntity.badRequest().build();
                }
            }
            CartResponse cart = cartService.addItemsToCart(requests);
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long cartItemId) {
        try {
            CartResponse cart = cartService.removeItemFromCart(cartItemId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/items")
    public ResponseEntity<CartResponse> clearCart() {
        try {
            CartResponse cart = cartService.clearCart();
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            throw e;
        }
    }
}