package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.RequestQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestQuotationRepository extends JpaRepository<RequestQuotation, Long> {
}
