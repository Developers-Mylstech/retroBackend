package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.response.WishlistResponse;
import java.util.List;

public interface WishlistService {
    /**
     * Get the current user's wishlist
     * @return The current user's wishlist
     */
    WishlistResponse getCurrentUserWishlist();
    
    /**
     * Add a product to the current user's wishlist
     * @param productId The ID of the product to add
     * @return The updated wishlist
     */
    WishlistResponse addProductToWishlist(Long productId);
    
    /**
     * Remove a product from the current user's wishlist
     * @param productId The ID of the product to remove
     * @return The updated wishlist
     */
    WishlistResponse removeProductFromWishlist(Long productId);
    
    /**
     * Check if a product is in the current user's wishlist
     * @param productId The ID of the product to check
     * @return true if the product is in the wishlist, false otherwise
     */
    boolean isProductInWishlist(Long productId);
    
    /**
     * Add multiple products to the current user's wishlist
     * @param productIds List of product IDs to add
     * @return The updated wishlist
     */
    WishlistResponse addProductsToWishlist(List<Long> productIds);
}
