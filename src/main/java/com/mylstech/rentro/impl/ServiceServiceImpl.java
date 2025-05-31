package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ServiceRequest;
import com.mylstech.rentro.dto.response.ServiceResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Service;
import com.mylstech.rentro.repository.ServiceRepository;
import com.mylstech.rentro.service.ServiceService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private static final String SERVICE_NOT_FOUND_WITH_ID = "Service not found with id: ";
    private final ServiceRepository serviceRepository;


    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll ( ).stream ( )
                .map ( ServiceResponse::new )
                .toList ( );
    }

    @Override
    public ServiceResponse getServiceById(Long id) {
        Service service = serviceRepository.findById ( id )
                .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );
        return new ServiceResponse ( service );
    }

    @Override
    public ServiceResponse createService(ServiceRequest request) {
        Service service = new Service ( );

        // Set related entities if IDs are provided
        if ( request != null ) {
            if ( request.getAmcBasic ( ) != null ) {
                service.setAmcBasic ( request.getAmcBasic ( ).requestToServiceField ( ) );
            }
            if ( request.getAmcGold ( ) != null ) {
                service.setAmcGold ( request.getAmcGold ( ).requestToServiceField ( ) );
            }
            if ( request.getMmc ( ) != null ) {
                service.setMmc ( request.getMmc ( ).requestToServiceField ( ) );
            }
            if ( request.getOts ( ) != null ) {
                service.setOts ( request.getOts ( ).requestToServiceField ( ) );
            }
            serviceRepository.save ( service );
        }

        return new ServiceResponse ( serviceRepository.save ( service ) );
    }

    @Override
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service service = serviceRepository.findById ( id )
                .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );

        if ( request != null ) {

            if ( request.getAmcBasic ( ) != null ) {
                service.setAmcBasic ( request.getAmcBasic ( ).requestToServiceField ( ) );
            }
            if ( request.getAmcGold ( ) != null ) {
                service.setAmcGold ( request.getAmcGold ( ).requestToServiceField ( ) );
            }
            if ( request.getMmc ( ) != null ) {
                service.setMmc ( request.getMmc ( ).requestToServiceField ( ) );
            }
            if ( request.getOts ( ) != null ) {
                service.setOts ( request.getOts ( ).requestToServiceField ( ) );
            }
            serviceRepository.save ( service );
        }

        return new ServiceResponse ( serviceRepository.save ( service ) );
    }

    @Override
    public void deleteService(Long id) {
        Service service = serviceRepository.findById ( id )
                .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );
        serviceRepository.delete ( service );
    }
}
