package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;
import com.mylstech.rentro.dto.response.OurServiceWithProductsResponse;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

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
    
    /**
     * Set the image for an our service
     * @param ourServiceId the our service ID
     * @param imageId the image ID
     * @return the updated our service response
     */
    @Transactional
    OurServiceResponse setOurServiceImage(Long ourServiceId, Long imageId);
    
    /**
     * Remove the image from an our service
     * @param ourServiceId the our service ID
     * @return the updated our service response
     */
    @Transactional
    OurServiceResponse removeOurServiceImage(Long ourServiceId);
}
