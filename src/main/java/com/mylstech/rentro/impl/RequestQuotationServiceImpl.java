package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.RequestQuotationRequest;
import com.mylstech.rentro.dto.response.RequestQuotationResponse;
import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.repository.RequestQuotationRepository;
import com.mylstech.rentro.service.RequestQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestQuotationServiceImpl implements RequestQuotationService {

    private final RequestQuotationRepository requestQuotationRepository;

    @Value("${vat.value}")
    private Double vat;

    @Override
    public List<RequestQuotationResponse> getAllRequestQuotations() {
        return requestQuotationRepository.findAll().stream().map(RequestQuotationResponse::new).toList();
    }

    @Override
    public RequestQuotationResponse getRequestQuotationById(Long id) {
        RequestQuotation requestQuotation = requestQuotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestQuotation not found with id: " + id));
        return new RequestQuotationResponse(requestQuotation);
    }

    @Override
    public RequestQuotationResponse createRequestQuotation(RequestQuotationRequest request) {
        RequestQuotation requestQuotation = request.requestToRequestQuotation();
        return new RequestQuotationResponse(requestQuotationRepository.save(requestQuotation));
    }

    @Override
    public RequestQuotationResponse updateRequestQuotation(Long id, RequestQuotationRequest request) {
        RequestQuotation requestQuotation = requestQuotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestQuotation not found with id: " + id));
        if (request.getCompanyName() != null ) {
            requestQuotation.setCompanyName(request.getCompanyName());
        }
        if (request.getName() != null ) {
            requestQuotation.setName(request.getName());
        }
        if (request.getMobile() != null ) {
            requestQuotation.setMobile(request.getMobile());
        }
        if (request.getLocation() != null ) {
            requestQuotation.setLocation(request.getLocation());
        }
        if (request.getProductImages() != null ) {
            requestQuotation.getProductImages().clear();
            requestQuotation.setProductImages(request.getProductImages());
        }

        return new RequestQuotationResponse(requestQuotationRepository.save(requestQuotation));
    }

    @Override
    public void deleteRequestQuotation(Long id) {
        RequestQuotation requestQuotation = requestQuotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RequestQuotation not found with id: " + id));
        requestQuotationRepository.delete(requestQuotation);
    }
}
