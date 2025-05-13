package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.RequestQuotationRequest;
import com.mylstech.rentro.dto.response.RequestQuotationResponse;
import com.mylstech.rentro.util.RequestQuotationStatus;

import java.util.List;

public interface RequestQuotationService {
    List<RequestQuotationResponse> getAllRequestQuotations();
    RequestQuotationResponse getRequestQuotationById(Long id);
    RequestQuotationResponse getRequestQuotationByCode(String code);
    List<RequestQuotationResponse> getRequestQuotationsByStatus(RequestQuotationStatus status);
    List<RequestQuotationResponse> searchRequestQuotationsByCompany(String companyName);
    RequestQuotationResponse createRequestQuotation(RequestQuotationRequest request);
    RequestQuotationResponse updateRequestQuotation(Long id, RequestQuotationRequest request);
    void deleteRequestQuotation(Long id);
}