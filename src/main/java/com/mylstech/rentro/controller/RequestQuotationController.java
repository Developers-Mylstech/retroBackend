package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.RequestQuotationRequest;
import com.mylstech.rentro.dto.response.RequestQuotationResponse;
import com.mylstech.rentro.service.RequestQuotationService;
import com.mylstech.rentro.util.RequestQuotationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/request-quotations")
@RequiredArgsConstructor
public class RequestQuotationController {

    private final RequestQuotationService requestQuotationService;

    @GetMapping
    public ResponseEntity<List<RequestQuotationResponse>> getAllRequestQuotations() {
        return ResponseEntity.ok(requestQuotationService.getAllRequestQuotations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestQuotationResponse> getRequestQuotationById(@PathVariable Long id) {
        return ResponseEntity.ok(requestQuotationService.getRequestQuotationById(id));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<RequestQuotationResponse> getRequestQuotationByCode(@PathVariable String code) {
        return ResponseEntity.ok(requestQuotationService.getRequestQuotationByCode(code));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestQuotationResponse>> getRequestQuotationsByStatus(
            @PathVariable RequestQuotationStatus status) {
        return ResponseEntity.ok(requestQuotationService.getRequestQuotationsByStatus(status));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<RequestQuotationResponse>> searchRequestQuotations(
            @RequestParam String companyName) {
        return ResponseEntity.ok(requestQuotationService.searchRequestQuotationsByCompany(companyName));
    }

    @PostMapping
    public ResponseEntity<RequestQuotationResponse> createRequestQuotation(@RequestBody RequestQuotationRequest request) {
        return new ResponseEntity<>(requestQuotationService.createRequestQuotation(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestQuotationResponse> updateRequestQuotation(
            @PathVariable Long id, 
            @RequestBody RequestQuotationRequest request) {
        return ResponseEntity.ok(requestQuotationService.updateRequestQuotation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequestQuotation(@PathVariable Long id) {
        requestQuotationService.deleteRequestQuotation(id);
        return ResponseEntity.noContent().build();
    }
}