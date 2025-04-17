package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.SpecificationFieldRequest;
import com.mylstech.rentro.dto.response.SpecificationFieldResponse;
import com.mylstech.rentro.model.SpecificationField;
import com.mylstech.rentro.repository.SpecificationFieldRepository;
import com.mylstech.rentro.service.SpecificationFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecificationFieldServiceImpl implements SpecificationFieldService {

    private final SpecificationFieldRepository specificationFieldRepository;

    @Override
    public List<SpecificationFieldResponse> getAllSpecificationFields() {
        return specificationFieldRepository.findAll().stream().map(SpecificationFieldResponse::new).toList();
    }

    @Override
    public SpecificationFieldResponse getSpecificationFieldById(Long id) {
        SpecificationField specificationField = specificationFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpecificationField not found with id: " + id));
        return new SpecificationFieldResponse(specificationField);
    }

    @Override
    public SpecificationFieldResponse createSpecificationField(SpecificationFieldRequest request) {
        SpecificationField specificationField = request.requestToSpecificationField();
        return new SpecificationFieldResponse(specificationFieldRepository.save(specificationField));
    }

    @Override
    public SpecificationFieldResponse updateSpecificationField(Long id, SpecificationFieldRequest request) {
        SpecificationField specificationField = specificationFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpecificationField not found with id: " + id));
        
        if (request.getName() != null) {
            specificationField.setName(request.getName());
        }
        
        return new SpecificationFieldResponse(specificationFieldRepository.save(specificationField));
    }

    @Override
    public void deleteSpecificationField(Long id) {
        SpecificationField specificationField = specificationFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpecificationField not found with id: " + id));
        specificationFieldRepository.delete(specificationField);
    }
}
