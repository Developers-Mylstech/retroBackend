package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    /**
     * Find a wishlist by user ID
     * @param userId The ID of the user
     * @return The wishlist, if found
     */
    Optional<Wishlist> findByUserUserId(Long userId);
    
    /**
     * Check if a product is in a user's wishlist
     * @param userId The ID of the user
     * @param productId The ID of the product
     * @return true if the product is in the wishlist, false otherwise
     */
    boolean existsByUserUserIdAndProductsProductId(Long userId, Long productId);


    @Query("SELECT DISTINCT w FROM Wishlist w JOIN w.products p WHERE p = :product")
    List<Wishlist> findByProductsContaining(@Param("product") Product product);
}