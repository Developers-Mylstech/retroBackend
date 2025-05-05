package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.BuyNowRequest;
import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.util.ProductType;

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
    
    // New method to get products by type
    List<ProductResponse> getProductsByType(ProductType productType);
    
    /**
     * Buy a product immediately and proceed to checkout
     * @param productId the product ID
     * @param request the buy now request
     * @return the checkout response
     */
    CheckOutResponse buyNow(Long productId, BuyNowRequest request);
}
