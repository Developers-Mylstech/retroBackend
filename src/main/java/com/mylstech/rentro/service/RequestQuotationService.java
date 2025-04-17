package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.RequestQuotationRequest;
import com.mylstech.rentro.dto.response.RequestQuotationResponse;

import java.util.List;

public interface RequestQuotationService {
    List<RequestQuotationResponse> getAllRequestQuotations();
    RequestQuotationResponse getRequestQuotationById(Long id);
    RequestQuotationResponse createRequestQuotation(RequestQuotationRequest request);
    RequestQuotationResponse updateRequestQuotation(Long id, RequestQuotationRequest request);
    void deleteRequestQuotation(Long id);
}
