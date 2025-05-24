package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartResponse;
import com.mylstech.rentro.model.Cart;

import java.util.List;

public interface CartService {
    /**
     * Get the current user's cart
     * @return the cart response
     */
    CartResponse getCurrentUserCart();
    
    /**
     * Add an item to the current user's cart
     * @param request the cart item request
     * @return the updated cart response
     */
    CartResponse addItemToCart(CartItemRequest request);
    
    /**
     * Add multiple items to the current user's cart
     * @param requests list of cart item requests
     * @return the updated cart response
     */
    CartResponse addItemsToCart(List<CartItemRequest> requests);
    
    /**
     * Remove an item from the current user's cart
     * @param cartItemId the cart item ID to remove
     * @return the updated cart response
     */
    CartResponse removeItemFromCart(Long cartItemId);
    
    /**
     * Clear all items from the current user's cart
     * @return the empty cart response
     */
    CartResponse clearCart();

    Cart getUserCart();
}