package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;

import java.util.List;

public interface OurServiceService {
    List<OurServiceResponse> getAllOurServices();
    OurServiceResponse getOurServiceById(Long id);
    OurServiceResponse createOurService(OurServiceRequest request);
    OurServiceResponse updateOurService(Long id, OurServiceRequest request);
    void deleteOurService(Long id);
}
