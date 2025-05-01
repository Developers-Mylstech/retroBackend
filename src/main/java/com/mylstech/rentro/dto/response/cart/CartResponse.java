package com.mylstech.rentro.dto.response.cart;

import com.mylstech.rentro.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private Double totalPrice;
    
    public CartResponse(Cart cart) {
        this.cartId = cart.getCartId();
        this.userId = cart.getUser().getUserId();
        this.items = cart.getItems().stream()
                .map(CartItemResponse::new)
                .toList();
        this.totalPrice = calculateTotalPrice();
    }
    private Double calculateTotalPrice() {
    return items.stream ().mapToDouble ( CartItemResponse::getPrice ).sum ();
    }
}