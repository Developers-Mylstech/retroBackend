package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartItemResponse;

public interface CartItemService {
    

    /**
     * Get a specific cart item by ID
     * @param id the cart item ID
     * @return the cart item response
     */
    CartItemResponse getCartItemById(Long id);
    
    /**
     * Add a new item to the current user's cart
     * @param request the cart item request
     * @return the created cart item response
     */
    CartItemResponse addToCart(CartItemRequest request);
    
    /**
     * Update an existing cart item
     * @param id the cart item ID
     * @param request the updated cart item request
     * @return the updated cart item response
     */
    CartItemResponse updateCartItem(Long id, CartItemRequest request);
    
    /**
     * Remove a cart item from the current user's cart
     * @param id the cart item ID to remove
     */
    void removeCartItem(Long id);
    
    /**
     * Recalculate prices for all cart items
     * This is useful when product prices change
     */
    void recalculateAllPrices();

    /**
     * Recalculate price for a specific cart item
     * @param cartItemId the ID of the cart item to recalculate
     * @return the updated cart item
     */
    CartItemResponse recalculatePrice(Long cartItemId);
}
