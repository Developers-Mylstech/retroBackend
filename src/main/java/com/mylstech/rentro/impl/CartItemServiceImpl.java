package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartItemResponse;
import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.CartItemRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.CartItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    @Override
    public CartItemResponse getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Cart item not found with id: " + id ) );
        return new CartItemResponse ( cartItem );
    }

    @Override
    @Transactional
    public CartItemResponse addItemToCart(CartItemRequest request) {
        // Find the product
        Product product = productRepository.findById ( request.getProductId ( ) )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + request.getProductId ( ) ) );

        CartItem cartItem = new CartItem ( );
        cartItem.setProduct ( product );

        // Update quantity or rent period based on product type
        switch (request.getProductType ( )) {
            case SELL:
                cartItem.setSellQuantity ( request.getSellQuantity ( ) );
                cartItem.setRentPeriod ( null );
                cartItem.setPrice ( product.getProductFor ( ).getSell ( ).getActualPrice ( )* cartItem.getSellQuantity ( ) );
                break;
            case RENT:
                cartItem.setRentPeriod ( request.getRentPeriod ( ) );
                cartItem.setSellQuantity ( 0 );
                cartItem.setPrice ( product.getProductFor ( ).getRent ( ).getMonthlyPrice ( )* cartItem.getRentPeriod ( ) );
                break;
            default:
                throw new IllegalArgumentException ( "Unsupported product type: " + request.getProductType ( ) );
        }

        cartItem.setProductType ( request.getProductType ( ) );
        cartItem = cartItemRepository.save ( cartItem );
        return new CartItemResponse ( cartItem );
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(Long cartItemId, CartItemRequest request) {

        // Find the cart item
        CartItem cartItem = cartItemRepository.findById ( cartItemId )
                .orElseThrow ( () -> new RuntimeException ( "Cart item not found with id: " + cartItemId ) );

        // Find the product if it's different
        if ( ! cartItem.getProduct ( ).getProductId ( ).equals ( request.getProductId ( ) ) ) {
            Product product = productRepository.findById ( request.getProductId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + request.getProductId ( ) ) );
            cartItem.setProduct ( product );
        }

        // Update cart item properties
        cartItem.setProductType ( request.getProductType ( ) );

        // Update quantity or rent period based on product type
        switch (request.getProductType ( )) {
            case SELL:
                cartItem.setSellQuantity ( request.getSellQuantity ( ) );
                cartItem.setRentPeriod ( null );
                cartItem.setPrice ( cartItem.getProduct ().getProductFor ( ).getSell ( ).getActualPrice ( )* cartItem.getSellQuantity ( ) );
                break;
            case RENT:
                cartItem.setRentPeriod ( request.getRentPeriod ( ) );
                cartItem.setSellQuantity ( 0 );
                cartItem.setPrice ( cartItem.getProduct ().getProductFor ( ).getRent ( ).getMonthlyPrice ( )* cartItem.getRentPeriod ( ) );
                break;
            default:
                throw new IllegalArgumentException ( "Unsupported product type: " + request.getProductType ( ) );
        }

        cartItem = cartItemRepository.save ( cartItem );
        return new CartItemResponse ( cartItem );
    }

    @Override
    @Transactional
    public void removeCartItem(Long id) {

        // Find the cart item
        CartItem cartItem = cartItemRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Cart item not found with id: " + id ) );
        cartItemRepository.delete ( cartItem );

    }


}