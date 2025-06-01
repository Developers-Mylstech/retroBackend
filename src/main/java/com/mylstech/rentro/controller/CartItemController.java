package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartItemResponse;
import com.mylstech.rentro.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
@Tag(name = "Cart Items", description = "Operations related to cart items")
public class CartItemController {

    private final CartItemService cartItemService;
    private final Logger logger = LoggerFactory.getLogger ( CartItemController.class );

    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponse> getCartItemById(@PathVariable Long id) {

            CartItemResponse cartItem = cartItemService.getCartItemById ( id );
            return ResponseEntity.ok ( cartItem );

    }

    @Operation(
            summary = "Add item to cart",
            description = "Add a product to the cart with specified quantity. For rental items, both quantity and rental period are used."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to cart successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestBody CartItemRequest request
    ) {

            logger.debug ( "Adding item to cart: {}", request );
            CartItemResponse response = cartItemService.addToCart ( request );
            logger.debug ( "Item added to cart with ID: {}", response.getCartItemId ( ) );
            return new ResponseEntity<> ( response, HttpStatus.CREATED );

    }

    @Operation(
            summary = "Update cart item",
            description = "Update quantity, rental period, or product type of an item in the cart"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long id,
            @RequestBody CartItemRequest request
    ) {

            logger.debug ( "Updating cart item with ID: {}", id );
            CartItemResponse response = cartItemService.updateCartItem ( id, request );
            logger.debug ( "Cart item updated: {}", response.getCartItemId ( ) );
            return ResponseEntity.ok ( response );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id) {

            cartItemService.removeCartItem ( id );
            return ResponseEntity.noContent ( ).build ( );

    }

}
