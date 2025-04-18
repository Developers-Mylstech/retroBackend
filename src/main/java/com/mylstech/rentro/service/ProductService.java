package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    
    // Additional methods for managing product images
    ProductResponse addImageToProduct(Long productId, String imageUrl);
    ProductResponse removeImageFromProduct(Long productId, String imageUrl);
}
