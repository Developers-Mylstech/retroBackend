package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Long> {
}
