package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ProductImagesRequest;
import com.mylstech.rentro.dto.response.ProductImagesResponse;

import java.util.List;

public interface ProductImagesService {
    List<ProductImagesResponse> getAllProductImages();
    ProductImagesResponse getProductImagesById(Long id);
    ProductImagesResponse createProductImages(ProductImagesRequest request);
    ProductImagesResponse updateProductImages(Long id, ProductImagesRequest request);
    void deleteProductImages(Long id);
    
    // Additional methods for managing image URLs
    ProductImagesResponse addImageUrl(Long id, String imageUrl);
    ProductImagesResponse removeImageUrl(Long id, String imageUrl);
}
