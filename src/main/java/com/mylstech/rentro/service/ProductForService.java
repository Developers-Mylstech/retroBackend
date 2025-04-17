package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ProductForRequest;
import com.mylstech.rentro.dto.response.ProductForResponse;

import java.util.List;

public interface ProductForService {
    List<ProductForResponse> getAllProductFors();
    ProductForResponse getProductForById(Long id);
    ProductForResponse createProductFor(ProductForRequest request);
    ProductForResponse updateProductFor(Long id, ProductForRequest request);
    void deleteProductFor(Long id);
}
