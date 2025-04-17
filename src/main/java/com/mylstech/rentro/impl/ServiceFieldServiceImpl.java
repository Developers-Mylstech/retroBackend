package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ServiceFieldRequest;
import com.mylstech.rentro.dto.response.ServiceFieldResponse;
import com.mylstech.rentro.model.ServiceField;
import com.mylstech.rentro.repository.ServiceFieldRepository;
import com.mylstech.rentro.service.ServiceFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceFieldServiceImpl implements ServiceFieldService {

    private final ServiceFieldRepository serviceFieldRepository;

    @Override
    public List<ServiceFieldResponse> getAllServiceFields() {
        return serviceFieldRepository.findAll().stream().map(ServiceFieldResponse::new).toList();
    }

    @Override
    public ServiceFieldResponse getServiceFieldById(Long id) {
        ServiceField serviceField = serviceFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + id));
        return new ServiceFieldResponse(serviceField);
    }

    @Override
    public ServiceFieldResponse createServiceField(ServiceFieldRequest request) {
        ServiceField serviceField = request.requestToServiceField();
        return new ServiceFieldResponse(serviceFieldRepository.save(serviceField));
    }

    @Override
    public ServiceFieldResponse updateServiceField(Long id, ServiceFieldRequest request) {
        ServiceField serviceField = serviceFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + id));
        
        if (request.getPrice() != null) {
            serviceField.setPrice(request.getPrice());
        }
        
        if (request.getBenefits() != null) {
            serviceField.setBenefits(request.getBenefits());
        }
        
        return new ServiceFieldResponse(serviceFieldRepository.save(serviceField));
    }

    @Override
    public void deleteServiceField(Long id) {
        ServiceField serviceField = serviceFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + id));
        serviceFieldRepository.delete(serviceField);
    }
}
