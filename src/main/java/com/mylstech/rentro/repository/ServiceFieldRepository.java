package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.ServiceField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceFieldRepository extends JpaRepository<ServiceField, Long> {
}
