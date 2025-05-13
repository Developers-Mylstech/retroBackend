package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Add custom query methods if needed
}