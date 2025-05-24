package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.response.WishlistResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.model.Wishlist;
import com.mylstech.rentro.repository.ProductRepository;
import com.mylstech.rentro.repository.WishlistRepository;
import com.mylstech.rentro.service.WishlistService;
import com.mylstech.rentro.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;
    private final Logger logger = LoggerFactory.getLogger ( WishlistServiceImpl.class );

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getCurrentUserWishlist() {
        try {
            logger.debug ( "Getting wishlist for current user" );
            AppUser currentUser = securityUtils.getCurrentUser ( );
            Wishlist wishlist = getOrCreateWishlist ( currentUser );
            logger.debug ( "Found wishlist with ID: {} for user: {}", wishlist.getWishlistId ( ), currentUser.getUserId ( ) );
            return new WishlistResponse ( wishlist );
        }
        catch ( Exception e ) {
            logger.error ( "Error getting wishlist for current user", e );
            throw new ResourceNotFoundException ( "Failed to get wishlist for current user" );
        }
    }

    @Override
    @Transactional
    public WishlistResponse addProductToWishlist(Long productId) {
        try {
            logger.debug ( "Adding product with ID: {} to wishlist", productId );
            AppUser currentUser = securityUtils.getCurrentUser ( );
            Wishlist wishlist = getOrCreateWishlist ( currentUser );

            addProductsInWishlist ( productId, wishlist );

            wishlist = wishlistRepository.save ( wishlist );
            return new WishlistResponse ( wishlist );
        }
        catch ( Exception e ) {
            logger.error ( "Error adding product to wishlist", e );
            throw new RuntimeException ( "Failed to add product to wishlist", e );
        }
    }

    private void addProductsInWishlist(Long productId, Wishlist wishlist) {
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + productId ) );

        boolean added = wishlist.addProduct ( product );
        if ( added ) {
            logger.debug ( "Product with ID: {} added to wishlist", productId );
        } else {
            logger.debug ( "Product with ID: {} already in wishlist", productId );
        }
    }

    @Override
    @Transactional
    public WishlistResponse removeProductFromWishlist(Long productId) {
        try {
            logger.debug ( "Removing product with ID: {} from wishlist", productId );
            AppUser currentUser = securityUtils.getCurrentUser ( );
            Wishlist wishlist = getOrCreateWishlist ( currentUser );

            Product product = productRepository.findById ( productId )
                    .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + productId ) );

            boolean removed = wishlist.removeProduct ( product );
            if ( removed ) {
                logger.debug ( "Product with ID: {} removed from wishlist", productId );
            } else {
                logger.debug ( "Product with ID: {} not in wishlist", productId );
            }

            wishlist = wishlistRepository.save ( wishlist );
            return new WishlistResponse ( wishlist );
        }
        catch ( Exception e ) {
            logger.error ( "Error removing product from wishlist", e );
            throw new RuntimeException ( "Failed to remove product from wishlist", e );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long productId) {
        try {
            logger.debug ( "Checking if product with ID: {} is in wishlist", productId );
            AppUser currentUser = securityUtils.getCurrentUser ( );
            return wishlistRepository.existsByUserUserIdAndProductsProductId ( currentUser.getUserId ( ), productId );
        }
        catch ( Exception e ) {
            logger.error ( "Error checking if product is in wishlist", e );
            throw new RuntimeException ( "Failed to check if product is in wishlist", e );
        }
    }

    @Transactional
    @Override
    public WishlistResponse addProductsToWishlist(List<Long> productIds) {
        try {
            AppUser currentUser = securityUtils.getCurrentUser ( );
            Wishlist wishlist = getOrCreateWishlist ( currentUser );
            for (Long productId : productIds) {
                addProductsInWishlist ( productId, wishlist );
            }
            wishlist = wishlistRepository.save ( wishlist );
            return new WishlistResponse ( wishlist );
        }
        catch ( Exception e ) {
            logger.error ( "Error adding product to wishlist", e );
            throw new RuntimeException ( "Failed to add product to wishlist", e );
        }
    }

    /**
     * Get the current user's wishlist or create a new one if it doesn't exist
     *
     * @param user The current user
     * @return The user's wishlist
     */
    private Wishlist getOrCreateWishlist(AppUser user) {
        return wishlistRepository.findByUserUserId ( user.getUserId ( ) )
                .orElseGet ( () -> {
                    logger.debug ( "Creating new wishlist for user: {}", user.getUserId ( ) );
                    Wishlist newWishlist = new Wishlist ( );
                    newWishlist.setUser ( user );
                    newWishlist.setProducts ( new ArrayList<> ( ) );
                    return wishlistRepository.save ( newWishlist );
                } );
    }
}