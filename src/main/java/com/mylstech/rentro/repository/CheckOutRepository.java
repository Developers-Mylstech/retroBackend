package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.CheckOut;
import com.mylstech.rentro.util.CHECKOUT_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {
    /**
     * Find checkouts by user ID
     * @param userId user ID
     * @return list of checkouts for the given user
     */
    @Query("SELECT c FROM CheckOut c WHERE c.cart.user.id = :userId")
    List<CheckOut> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find checkouts by status
     * @param status checkout status
     * @return list of checkouts with the given status
     */
    List<CheckOut> findByStatus(CHECKOUT_STATUS status);
    
    /**
     * Find checkouts by user ID and status
     * @param userId user ID
     * @param status checkout status
     * @return list of checkouts for the given user with the given status
     */
    @Query("SELECT c FROM CheckOut c WHERE c.cart.user.id = :userId AND c.status = :status")
    List<CheckOut> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") CHECKOUT_STATUS status);
}
