package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;
import com.mylstech.rentro.dto.response.OurServiceWithProductsResponse;

import java.util.List;

public interface OurServiceService {
    List<OurServiceResponse> getAllOurServices();
    OurServiceResponse getOurServiceById(Long id);
    OurServiceResponse createOurService(OurServiceRequest request);
    OurServiceResponse updateOurService(Long id, OurServiceRequest request);
    void deleteOurService(Long id);
    /**
     * Get a service by ID with its related products
     * @param id The ID of the service
     * @return The service with its related products
     */
    OurServiceWithProductsResponse getOurServiceWithProducts(Long id);
}
