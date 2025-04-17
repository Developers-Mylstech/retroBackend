package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ServiceRequest;
import com.mylstech.rentro.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceService {
    List<ServiceResponse> getAllServices();
    ServiceResponse getServiceById(Long id);
    ServiceResponse createService(ServiceRequest request);
    ServiceResponse updateService(Long id, ServiceRequest request);
    void deleteService(Long id);
}
