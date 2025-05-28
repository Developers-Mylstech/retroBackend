package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartItemResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.CartItemRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.CartItemService;
import com.mylstech.rentro.util.ProductType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private static final String CART_ITEM_NOT_FOUND_WITH_ID = "Cart item not found with id: ";
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private static void validateCartItemRequestWithProduct(CartItemRequest request, Product product) {
        if ( product.getProductFor ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not properly configured for purchase or rental" );
        }

        // Validate product type compatibility
        if ( request.getProductType ( ) == ProductType.SELL && product.getProductFor ( ).getSell ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not available for purchase: " + product.getProductId ( ) );
        }

        if ( request.getProductType ( ) == ProductType.RENT && product.getProductFor ( ).getRent ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not available for rent: " + product.getProductId ( ) );
        }

        if ( request.getProductType ( ) == ProductType.OTS && product.getProductFor ( ).getServices ( ).getOts ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not available for OTS: " + product.getProductId ( ) );
        }
        if ( request.getProductType ( ) == ProductType.MMC && product.getProductFor ( ).getServices ( ).getMmc ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not available for MMC: " + product.getProductId ( ) );
        }
        if ( request.getProductType ( ) == ProductType.AMC_BASIC && product.getProductFor ( ).getServices ( ).getAmcBasic ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not available for AMC BASIC: " + product.getProductId ( ) );
        }
        if ( request.getProductType ( ) == ProductType.AMC_GOLD && product.getProductFor ( ).getServices ( ).getAmcGold ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not available for AMC GOLD: " + product.getProductId ( ) );
        }
    }

    @Override
    public CartItemResponse getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById ( id )
                .orElseThrow ( () -> new ResourceNotFoundException ( CART_ITEM_NOT_FOUND_WITH_ID + id ) );
        return new CartItemResponse ( cartItem );
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(CartItemRequest request) {
        Product product = productRepository.findById ( request.getProductId ( ) )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + request.getProductId ( ) ) );

        // Validate product configuration
        validateCartItemRequestWithProduct ( request, product );


        CartItem cartItem = new CartItem ( );
        cartItem.setProduct ( product );
        cartItem.setProductType ( request.getProductType ( ) );
        // Set quantity for both SELL and RENT
        cartItem.setQuantity ( request.getQuantity ( ) );


        if ( request.getProductType ( ) == ProductType.RENT ) {
            // Calculate price for rent item
            double monthlyPrice = product.getProductFor ( ).getRent ( ).getDiscountPrice ( );
            if ( monthlyPrice <= 0 ) {
                monthlyPrice = product.getProductFor ( ).getRent ( ).getMonthlyPrice ( );
            }
            cartItem.setPrice ( monthlyPrice * cartItem.getQuantity ( ) );
        } else if ( request.getProductType ( ) == ProductType.SELL ) {
            // Calculate price for sell item
            double unitPrice = product.getProductFor ( ).getSell ( ).getDiscountPrice ( );
            if ( unitPrice <= 0 ) {
                unitPrice = product.getProductFor ( ).getSell ( ).getActualPrice ( );
            }
            cartItem.setPrice ( unitPrice * cartItem.getQuantity ( ) );
        } else if ( request.getProductType ( ) == ProductType.OTS ) {
            cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getOts ( ).getPrice ( ) );
        } else if ( request.getProductType ( ) == ProductType.MMC ) {
            cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getMmc ( ).getPrice ( ) );

        } else if ( request.getProductType ( ) == ProductType.AMC_GOLD ) {
            cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getAmcBasic ( ).getPrice ( ) );

        } else if ( request.getProductType ( ) == ProductType.AMC_BASIC ) {
            cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getAmcGold ( ).getPrice ( ) );
        }
        CartItem savedCartItem = cartItemRepository.save ( cartItem );
        return new CartItemResponse ( savedCartItem );
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(Long id, CartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( CART_ITEM_NOT_FOUND_WITH_ID + id ) );


        // Set quantity for both SELL and RENT
        if ( request.getQuantity ( ) != null ) {
            cartItem.setQuantity ( request.getQuantity ( ) );
        }
        if ( cartItem.getProductType ( ) == ProductType.RENT ) {
            cartItem.setPrice ( cartItem.getProduct ( ).getProductFor ( ).getRent ( ).getDiscountPrice ( ) * cartItem.getQuantity ( ) );
        } else if ( cartItem.getProductType ( ) == ProductType.SELL ) {
            cartItem.setPrice ( cartItem.getProduct ( ).getProductFor ( ).getSell ( ).getDiscountPrice ( ) * cartItem.getQuantity ( ) );
        }
        CartItem updatedCartItem = cartItemRepository.save ( cartItem );
        return new CartItemResponse ( updatedCartItem );
    }

    @Override
    @Transactional
    public void removeCartItem(Long id) {

        // Find the cart item
        CartItem cartItem = cartItemRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( CART_ITEM_NOT_FOUND_WITH_ID + id ) );
        cartItemRepository.delete ( cartItem );

    }

    @Override
    @Transactional
    public void recalculateAllPrices() {
        List<CartItem> cartItems = cartItemRepository.findAll ( );
        for (CartItem cartItem : cartItems) {
            recalculateCartItemPrice ( cartItem );
            cartItemRepository.save ( cartItem );
        }
    }

    @Override
    @Transactional
    public CartItemResponse recalculatePrice(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById ( cartItemId )
                .orElseThrow ( () -> new RuntimeException ( CART_ITEM_NOT_FOUND_WITH_ID + cartItemId ) );

        recalculateCartItemPrice ( cartItem );
        CartItem updatedCartItem = cartItemRepository.save ( cartItem );
        return new CartItemResponse ( updatedCartItem );
    }

    /**
     * Helper method to recalculate the price of a cart item
     */
    private void recalculateCartItemPrice(CartItem cartItem) {
        if ( cartItem.getProduct ( ) == null || cartItem.getProduct ( ).getProductFor ( ) == null ) {
            cartItem.setPrice ( 0.0 );
            return;
        }

        if ( cartItem.getProductType ( ) == ProductType.SELL &&
                cartItem.getProduct ( ).getProductFor ( ).getSell ( ) != null ) {
            double unitPrice = cartItem.getProduct ( ).getProductFor ( ).getSell ( ).getDiscountPrice ( );
            if ( unitPrice <= 0 ) {
                unitPrice = cartItem.getProduct ( ).getProductFor ( ).getSell ( ).getActualPrice ( );
            }
            cartItem.setPrice ( unitPrice * cartItem.getQuantity ( ) );
        } else if ( cartItem.getProductType ( ) == ProductType.RENT &&
                cartItem.getProduct ( ).getProductFor ( ).getRent ( ) != null ) {
            double monthlyPrice = cartItem.getProduct ( ).getProductFor ( ).getRent ( ).getDiscountPrice ( );
            if ( monthlyPrice <= 0 ) {
                monthlyPrice = cartItem.getProduct ( ).getProductFor ( ).getRent ( ).getMonthlyPrice ( );
            }
            cartItem.setPrice ( monthlyPrice * cartItem.getQuantity ( ) );
        } else {
            cartItem.setPrice ( 0.0 );
        }
    }
}
