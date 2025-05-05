package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Find a cart by user ID
     * @param userId the user ID
     * @return the cart
     */
    Optional<Cart> findByUserUserId(Long userId);
    
    /**
     * Find a non-temporary cart by user ID
     * @param userId the user ID
     * @return the cart
     */
    Optional<Cart> findByUserUserIdAndTemporaryFalse(Long userId);
}
