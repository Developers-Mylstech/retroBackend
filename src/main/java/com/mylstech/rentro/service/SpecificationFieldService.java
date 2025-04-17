package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.SpecificationFieldRequest;
import com.mylstech.rentro.dto.response.SpecificationFieldResponse;

import java.util.List;

public interface SpecificationFieldService {
    List<SpecificationFieldResponse> getAllSpecificationFields();
    SpecificationFieldResponse getSpecificationFieldById(Long id);
    SpecificationFieldResponse createSpecificationField(SpecificationFieldRequest request);
    SpecificationFieldResponse updateSpecificationField(Long id, SpecificationFieldRequest request);
    void deleteSpecificationField(Long id);
}
