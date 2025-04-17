package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.SpecificationRequest;
import com.mylstech.rentro.dto.response.SpecificationResponse;

import java.util.List;

public interface SpecificationService {
    List<SpecificationResponse> getAllSpecifications();
    SpecificationResponse getSpecificationById(Long id);
    SpecificationResponse createSpecification(SpecificationRequest request);
    SpecificationResponse updateSpecification(Long id, SpecificationRequest request);
    void deleteSpecification(Long id);
}
