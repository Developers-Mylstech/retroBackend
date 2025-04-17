package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ServiceFieldRequest;
import com.mylstech.rentro.dto.response.ServiceFieldResponse;

import java.util.List;

public interface ServiceFieldService {
    List<ServiceFieldResponse> getAllServiceFields();
    ServiceFieldResponse getServiceFieldById(Long id);
    ServiceFieldResponse createServiceField(ServiceFieldRequest request);
    ServiceFieldResponse updateServiceField(Long id, ServiceFieldRequest request);
    void deleteServiceField(Long id);
}
