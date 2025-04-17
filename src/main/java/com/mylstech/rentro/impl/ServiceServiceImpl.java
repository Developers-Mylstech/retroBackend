package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ServiceRequest;
import com.mylstech.rentro.dto.response.ServiceResponse;
import com.mylstech.rentro.model.Service;
import com.mylstech.rentro.model.ServiceField;
import com.mylstech.rentro.repository.ServiceFieldRepository;
import com.mylstech.rentro.repository.ServiceRepository;
import com.mylstech.rentro.service.ServiceService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceFieldRepository serviceFieldRepository;

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(ServiceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        return new ServiceResponse(service);
    }

    @Override
    public ServiceResponse createService(ServiceRequest request) {
        Service service = new Service();
        
        // Set related entities if IDs are provided
        if (request.getOtsId() != null) {
            ServiceField ots = serviceFieldRepository.findById(request.getOtsId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getOtsId()));
            service.setOts(ots);
        }
        
        if (request.getMmcId() != null) {
            ServiceField mmc = serviceFieldRepository.findById(request.getMmcId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getMmcId()));
            service.setMmc(mmc);
        }
        
        if (request.getAmcBasicId() != null) {
            ServiceField amcBasic = serviceFieldRepository.findById(request.getAmcBasicId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getAmcBasicId()));
            service.setAmcBasic(amcBasic);
        }
        
        if (request.getAmcGoldId() != null) {
            ServiceField amcGold = serviceFieldRepository.findById(request.getAmcGoldId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getAmcGoldId()));
            service.setAmcGold(amcGold);
        }
        
        return new ServiceResponse(serviceRepository.save(service));
    }

    @Override
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        
        // Update related entities if IDs are provided
        if (request.getOtsId() != null) {
            ServiceField ots = serviceFieldRepository.findById(request.getOtsId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getOtsId()));
            service.setOts(ots);
        }
        
        if (request.getMmcId() != null) {
            ServiceField mmc = serviceFieldRepository.findById(request.getMmcId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getMmcId()));
            service.setMmc(mmc);
        }
        
        if (request.getAmcBasicId() != null) {
            ServiceField amcBasic = serviceFieldRepository.findById(request.getAmcBasicId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getAmcBasicId()));
            service.setAmcBasic(amcBasic);
        }
        
        if (request.getAmcGoldId() != null) {
            ServiceField amcGold = serviceFieldRepository.findById(request.getAmcGoldId())
                    .orElseThrow(() -> new RuntimeException("ServiceField not found with id: " + request.getAmcGoldId()));
            service.setAmcGold(amcGold);
        }
        
        return new ServiceResponse(serviceRepository.save(service));
    }

    @Override
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        serviceRepository.delete(service);
    }
}
