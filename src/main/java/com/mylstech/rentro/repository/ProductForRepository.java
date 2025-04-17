package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.ProductFor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductForRepository extends JpaRepository<ProductFor, Long> {
}
