package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.SpecificationRequest;
import com.mylstech.rentro.dto.response.SpecificationResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Specification;
import com.mylstech.rentro.repository.SpecificationRepository;
import com.mylstech.rentro.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecificationServiceImpl implements SpecificationService {

    private static final String SPECIFICATION_NOT_FOUND_WITH_ID = "Specification not found with id:";
    private final SpecificationRepository specificationRepository;

    @Override
    public List<SpecificationResponse> getAllSpecifications() {
        return specificationRepository.findAll ( ).stream ( ).map ( SpecificationResponse::new ).toList ( );
    }

    @Override
    public SpecificationResponse getSpecificationById(Long id) {
        Specification specification = specificationRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( SPECIFICATION_NOT_FOUND_WITH_ID + " " + id ) );
        return new SpecificationResponse ( specification );
    }

    @Override
    public SpecificationResponse createSpecification(SpecificationRequest request) {
        Specification specification = request.requestToSpecification ( );
        return new SpecificationResponse ( specificationRepository.save ( specification ) );
    }

    @Override
    public SpecificationResponse updateSpecification(Long id, SpecificationRequest request) {
        Specification specification = specificationRepository.findById ( id )
                .orElseThrow ( () -> new ResourceNotFoundException ( SPECIFICATION_NOT_FOUND_WITH_ID + " " + id ) );

        if ( request.getName ( ) != null ) {
            specification.setName ( request.getName ( ) );
        }

        if ( request.getValue ( ) != null ) {
            specification.setValue ( request.getValue ( ) );
        }

        return new SpecificationResponse ( specificationRepository.save ( specification ) );
    }

    @Override
    public void deleteSpecification(Long id) {
        Specification specification = specificationRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( SPECIFICATION_NOT_FOUND_WITH_ID + " " + id ) );
        specificationRepository.delete ( specification );
    }


}
