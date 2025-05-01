package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartItemResponse;
import com.mylstech.rentro.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CartItemController {

    private final CartItemService cartItemService;

    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponse> getCartItemById(@PathVariable Long id) {
        try {
            CartItemResponse cartItem = cartItemService.getCartItemById ( id );
            return ResponseEntity.ok ( cartItem );
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            if ( ! request.isValid ( ) ) {
                return ResponseEntity.badRequest ( ).build ( );
            }
            CartItemResponse cartItem = cartItemService.addItemToCart ( request );
            return new ResponseEntity<> ( cartItem, HttpStatus.CREATED );
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody CartItemRequest request) {
        try {
            if ( ! request.isValid ( ) ) {
                return ResponseEntity.badRequest ( ).build ( );
            }
            CartItemResponse cartItem = cartItemService.updateCartItem ( id, request );
            return ResponseEntity.ok ( cartItem );
        }
        catch ( Exception e ) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id) {
        try {
           cartItemService.removeCartItem ( id );
            return ResponseEntity.noContent ( ).build ( );
        }
        catch ( Exception e ) {
           throw e;
        }
    }

}
