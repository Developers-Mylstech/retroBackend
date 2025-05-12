package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.response.WishlistResponse;
import com.mylstech.rentro.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Operations related to user wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final Logger logger = LoggerFactory.getLogger(WishlistController.class);
    
    @GetMapping
    @Operation(summary = "Get current user's wishlist", description = "Returns the current user's wishlist with all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved wishlist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
    })
    public ResponseEntity<WishlistResponse> getCurrentUserWishlist() {
        try {
            logger.debug("Getting current user's wishlist");
            WishlistResponse wishlist = wishlistService.getCurrentUserWishlist();
            logger.debug("Found wishlist with {} products", 
                    wishlist.getProducts() != null ? wishlist.getProducts().size() : 0);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            logger.error("Error getting current user's wishlist", e);
            throw e;
        }
    }
    
    @PostMapping("/products/{productId}")
    @Operation(summary = "Add product to wishlist", description = "Adds a product to the current user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added product to wishlist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<WishlistResponse> addProductToWishlist(@PathVariable Long productId) {
        try {
            logger.debug("Adding product with ID: {} to wishlist", productId);
            WishlistResponse wishlist = wishlistService.addProductToWishlist(productId);
            logger.debug("Product added to wishlist, now has {} products", 
                    wishlist.getProducts() != null ? wishlist.getProducts().size() : 0);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            logger.error("Error adding product to wishlist", e);
            throw e;
        }
    }
    
    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Remove product from wishlist", description = "Removes a product from the current user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed product from wishlist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<WishlistResponse> removeProductFromWishlist(@PathVariable Long productId) {
        try {
            logger.debug("Removing product with ID: {} from wishlist", productId);
            WishlistResponse wishlist = wishlistService.removeProductFromWishlist(productId);
            logger.debug("Product removed from wishlist, now has {} products", 
                    wishlist.getProducts() != null ? wishlist.getProducts().size() : 0);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            logger.error("Error removing product from wishlist", e);
            throw e;
        }
    }
    
    @GetMapping("/products/{productId}")
    @Operation(summary = "Check if product is in wishlist", description = "Checks if a product is in the current user's wishlist")
    @ApiResponses

    public ResponseEntity<Boolean> isProductInWishlist(@PathVariable Long productId) {
        try {
            logger.debug("Checking if product with ID: {} is in wishlist", productId);
            boolean inWishlist = wishlistService.isProductInWishlist(productId);
            logger.debug("Product with ID: {} is {} in wishlist", productId, inWishlist ? "present" : "not present");
            return ResponseEntity.ok(inWishlist);
        } catch (Exception e) {
            logger.error("Error checking if product is in wishlist", e);
            throw e;
        }
    }

    @PostMapping("/products/batch")
    @Operation(summary = "Add multiple products to wishlist", description = "Adds multiple products to the current user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added products to wishlist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated"),
            @ApiResponse(responseCode = "404", description = "One or more products not found")
    })
    public ResponseEntity<WishlistResponse> addProductsToWishlist(@RequestBody List<Long> productIds) {
        try {
            logger.debug("Adding {} products to wishlist", productIds.size());
            WishlistResponse wishlist = wishlistService.addProductsToWishlist(productIds);
            logger.debug("Products added to wishlist, now has {} products", 
                    wishlist.getProducts() != null ? wishlist.getProducts().size() : 0);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            logger.error("Error adding products to wishlist", e);
            throw e;
        }
    }
}
