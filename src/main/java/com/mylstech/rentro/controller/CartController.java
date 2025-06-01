package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.CartItemRequest;
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

        CartResponse cart = cartService.getCurrentUserCart ( );
        return ResponseEntity.ok ( cart );

    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@Valid @RequestBody CartItemRequest request) {


        CartResponse cart = cartService.addItemToCart ( request );
        return new ResponseEntity<> ( cart, HttpStatus.CREATED );

    }

    @PostMapping("/items/batch")
    public ResponseEntity<CartResponse> addItemsToCart(@Valid @RequestBody List<CartItemRequest> requests) {
        // Validate all requests

        CartResponse cart = cartService.addItemsToCart ( requests );
        return new ResponseEntity<> ( cart, HttpStatus.CREATED );

    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long cartItemId) {

        CartResponse cart = cartService.removeItemFromCart ( cartItemId );
        return ResponseEntity.ok ( cart );
    }

    @DeleteMapping("/items")
    public ResponseEntity<CartResponse> clearCart() {

        CartResponse cart = cartService.clearCart ( );
        return ResponseEntity.ok ( cart );

    }
}