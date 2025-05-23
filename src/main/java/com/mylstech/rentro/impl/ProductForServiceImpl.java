package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ProductForRequest;
import com.mylstech.rentro.dto.response.ProductForResponse;
import com.mylstech.rentro.model.ProductFor;
import com.mylstech.rentro.model.Rent;
import com.mylstech.rentro.model.RequestQuotation;
import com.mylstech.rentro.model.Sell;
import com.mylstech.rentro.model.Service;
import com.mylstech.rentro.repository.ProductForRepository;
import com.mylstech.rentro.repository.RentRepository;
import com.mylstech.rentro.repository.RequestQuotationRepository;
import com.mylstech.rentro.repository.SellRepository;
import com.mylstech.rentro.repository.ServiceRepository;
import com.mylstech.rentro.service.ProductForService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ProductForServiceImpl implements ProductForService {

    private final ProductForRepository productForRepository;
    private final SellRepository sellRepository;
    private final RentRepository rentRepository;
    private final RequestQuotationRepository requestQuotationRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public List<ProductForResponse> getAllProductFors() {
        return productForRepository.findAll().stream()
                .map(ProductForResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProductForResponse getProductForById(Long id) {
        ProductFor productFor = productForRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductFor not found with id: " + id));
        return new ProductForResponse(productFor);
    }

    @Override
    public ProductForResponse createProductFor(ProductForRequest request) {
        ProductFor productFor = new ProductFor();
        
        // Set related entities if IDs are provided
        if (request.getSell() != null) {
            productFor.setSell(request.getSell().requestToSell());
        }
        
        if (request.getRent() != null) {

            productFor.setRent(request.getRent().requestToRent());
        }


        
        return new ProductForResponse(productForRepository.save(productFor));
    }

    @Override
    public ProductForResponse updateProductFor(Long id, ProductForRequest request) {
        ProductFor productFor = productForRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductFor not found with id: " + id));
        
        // Update related entities if IDs are provided
        if (request.getSell() != null) {
            productFor.setSell(request.getSell().requestToSell());
        }
        
        if (request.getRent() != null) {

            productFor.setRent(request.getRent().requestToRent());
        }


        return new ProductForResponse(productForRepository.save(productFor));
    }

    @Override
    public void deleteProductFor(Long id) {
        ProductFor productFor = productForRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductFor not found with id: " + id));
        productForRepository.delete(productFor);
    }
}
