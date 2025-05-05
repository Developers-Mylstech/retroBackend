package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.CheckOut;

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
    


}
