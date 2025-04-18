package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.SpecificationField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationFieldRepository extends JpaRepository<SpecificationField, Long> {
    boolean existsByName(String name);
}
