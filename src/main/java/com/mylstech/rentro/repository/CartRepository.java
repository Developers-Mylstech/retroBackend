package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(AppUser user);
}
