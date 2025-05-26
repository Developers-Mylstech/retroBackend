package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.cart.CartItemRequest;
import com.mylstech.rentro.dto.response.cart.CartResponse;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.Cart;
import com.mylstech.rentro.model.CartItem;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.CartItemRepository;
import com.mylstech.rentro.repository.CartRepository;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.service.CartService;
import com.mylstech.rentro.util.ProductType;
import com.mylstech.rentro.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger ( CartServiceImpl.class );
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;

    @Override
    public CartResponse getCurrentUserCart() {
        AppUser currentUser = securityUtils.getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );
        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(CartItemRequest request) {
        logger.debug ( "Adding item to cart: {}", request );

        AppUser currentUser = securityUtils.getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        // Use the helper method
        Cart cart1 = addItemToCartInternal ( request, cart );

        // Calculate total price for cart
        double totalPrice = cart1.getItems ( ).stream ( )
                .mapToDouble ( item -> item.getPrice ( ) != null ? item.getPrice ( ) : 0.0 )
                .sum ( );
        cart1.setTotalPrice ( totalPrice );

        cart = cartRepository.save ( cart1 );

        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse addItemsToCart(List<CartItemRequest> requests) {
        logger.debug ( "Adding multiple items to cart: {} items", requests != null ? requests.size ( ) : 0 );

        if ( requests == null || requests.isEmpty ( ) ) {
            throw new IllegalArgumentException ( "Cart item requests cannot be empty" );
        }


        AppUser currentUser = securityUtils.getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        // Process each request using the helper method
        for (CartItemRequest request : requests) {
            addItemToCartInternal ( request, cart );
        }

        // Calculate total price for cart
        double totalPrice = cart.getItems ( ).stream ( )
                .mapToDouble ( item -> item.getPrice ( ) != null ? item.getPrice ( ) : 0.0 )
                .sum ( );
        cart.setTotalPrice ( totalPrice );

        // Save the cart once with all new items
        cart = cartRepository.save ( cart );

        logger.debug ( "Cart updated with {} items, total price: {}",
                cart.getItems ( ).size ( ),
                cart.getTotalPrice ( ) );

        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long cartItemId) {
        AppUser currentUser = securityUtils.getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        // Find the cart item
        CartItem cartItem = cartItemRepository.findById ( cartItemId )
                .orElseThrow ( () -> new RuntimeException ( "Cart item not found with id: " + cartItemId ) );

        // Verify the cart item belongs to the current user's cart
        if ( ! cartItem.getCart ( ).getCartId ( ).equals ( cart.getCartId ( ) ) ) {
            throw new RuntimeException ( "You don't have permission to remove this item" );
        }

        // Remove the item from the cart
        cart.removeItem ( cartItem );

        // Recalculate total price
        cart.calculateTotalPrice ( );

        // Save the cart
        cart = cartRepository.save ( cart );

        return new CartResponse ( cart );
    }

    @Override
    @Transactional
    public CartResponse clearCart() {
        AppUser currentUser = securityUtils.getCurrentUser ( );
        Cart cart = getOrCreateCart ( currentUser );

        // Clear all items
        List<CartItem> itemsToRemove = new ArrayList<> ( cart.getItems ( ) );
        for (CartItem item : itemsToRemove) {
            cart.removeItem ( item );
        }

        // Reset total price
        cart.setTotalPrice ( 0.0 );

        // Save the cart
        cart = cartRepository.save ( cart );

        return new CartResponse ( cart );
    }

    /**
     * Get the user's cart or create a new one if it doesn't exist
     *
     * @param user the user
     * @return the cart
     */
    private Cart getOrCreateCart(AppUser user) {
        return cartRepository.findByUserUserIdAndTemporaryFalse ( user.getUserId ( ) )
                .orElseGet ( () -> {
                    Cart newCart = new Cart ( );
                    newCart.setUser ( user );
                    newCart.setItems ( new ArrayList<> ( ) );
                    newCart.setTotalPrice ( 0.0 );
                    newCart.setTemporary ( false );
                    return cartRepository.save ( newCart );
                } );
    }

    /**
     * Helper method to add an item to a cart without saving the cart
     *
     * @param request The cart item request
     * @param cart    The cart to add the item to
     */
    private Cart addItemToCartInternal(CartItemRequest request, Cart cart) {
        // Find the product
        Product product = productRepository.findById ( request.getProductId ( ) )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + request.getProductId ( ) ) );

        logger.debug ( "Found product: id={}, name={}, productFor={}",
                product.getProductId ( ),
                product.getName ( ),
                product.getProductFor ( ) != null ? "present" : "null" );

        // Validate product configuration
        if ( product.getProductFor ( ) == null ) {
            throw new IllegalArgumentException ( "Product is not properly configured for purchase or rental" );
        }
        // Validate product type compatibility
        if ( request.getProductType ( ) == ProductType.SELL &&
                (product.getProductFor ( ).getSell ( ) == null) ) {
            throw new IllegalArgumentException ( "This product is not available for purchase" );
        }

        if ( request.getProductType ( ) == ProductType.RENT &&
                (product.getProductFor ( ).getRent ( ) == null) ) {
            throw new IllegalArgumentException ( "This product is not available for rent" );
        }
        // Create the cart item
        CartItem cartItem = new CartItem ( );
        cartItem.setCart ( cart );
        cartItem.setProduct ( product );
        cartItem.setProductType ( request.getProductType ( ) );
        cartItem.setQuantity ( request.getQuantity ( )!=null ? request.getQuantity ( ) : 1 );

        // Set quantity and rent period based on product type
        if ( request.getProductType ( ) == ProductType.SELL ) {
            // Calculate price for sell item
            double unitPrice = product.getProductFor ( ).getSell ( ).getDiscountPrice ( );
            if ( unitPrice <= 0 ) {
                unitPrice = product.getProductFor ( ).getSell ( ).getActualPrice ( );
            }
            cartItem.setPrice ( unitPrice * cartItem.getQuantity ( ) );

        } else if ( request.getProductType ( ) == ProductType.RENT ) {

            // Calculate price for rent item
            double monthlyPrice = product.getProductFor ( ).getRent ( ).getDiscountPrice ( );
            if ( monthlyPrice <= 0 ) {
                monthlyPrice = product.getProductFor ( ).getRent ( ).getMonthlyPrice ( );
            }
            cartItem.setPrice ( monthlyPrice * cartItem.getQuantity ( ) );
        }

        logger.debug ( "Created cart item: type={}, price={}",
                cartItem.getProductType ( ),
                cartItem.getPrice ( ) );
logger.info("before adding in cart item Quantity {}",cartItem.getQuantity());
        cart.addItem (  cartItemRepository.save ( cartItem ) ) ;
logger.info("after adding in cart  item Quantity {}",cart.getItems().get(cart.getItems().size()-1).getQuantity());
    return cart;
    }

    @Override
    public Cart getUserCart() {
        logger.debug ( "Fetching cart for current user" );

        AppUser currentUser = securityUtils.getCurrentUser ( );

        // Find the user's non-temporary cart
        return cartRepository.findByUserUserIdAndTemporaryFalse ( currentUser.getUserId ( ) )
                .orElseGet ( () -> {
                    // Create a new cart if none exists
                    Cart newCart = new Cart ( );
                    newCart.setUser ( currentUser );
                    newCart.setItems ( new ArrayList<> ( ) );
                    newCart.setTotalPrice ( 0.0 );
                    newCart.setTemporary ( false );
                    return cartRepository.save ( newCart );
                } );


    }
}
