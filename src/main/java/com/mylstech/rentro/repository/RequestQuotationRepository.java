package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.util.RequestQuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestQuotationRepository extends JpaRepository<RequestQuotation, Long> {
    Optional<RequestQuotation> findByRequestQuotationCode(String code);
    List<RequestQuotation> findByStatus(RequestQuotationStatus status);
    List<RequestQuotation> findByCompanyNameContainingIgnoreCase(String companyName);
    
    @Query("SELECT r.requestQuotationCode FROM RequestQuotation r WHERE r.requestQuotationCode LIKE :prefix% ORDER BY r.requestQuotationCode DESC")
    List<String> findLatestCodesByPrefix(@Param("prefix") String prefix, org.springframework.data.domain.Pageable pageable);
}
