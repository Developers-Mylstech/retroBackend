package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find products available for sell
    @Query("SELECT p FROM Product p WHERE p.productFor.sell IS NOT NULL")
    List<Product> findByProductForSellNotNull();
    
    // Find products available for rent
    @Query("SELECT p FROM Product p WHERE p.productFor.rent IS NOT NULL")
    List<Product> findByProductForRentNotNull();
    
    // Find products available for service
    @Query("SELECT p FROM Product p WHERE p.productFor.services IS NOT NULL")
    List<Product> findByProductForServicesNotNull();
}
