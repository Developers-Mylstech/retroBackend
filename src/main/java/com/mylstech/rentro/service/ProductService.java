package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.BuyNowRequest;
import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.util.ProductType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    
    // Additional methods for managing product images
    ProductResponse addImageToProduct(Long productId, Long imageId);
    ProductResponse removeImageFromProduct(Long productId, Long imageId);


    // New method to get products by type
    List<ProductResponse> getProductsByType(ProductType productType);
    
    /**
     * Buy a product immediately and proceed to checkout
     * @param productId the product ID
     * @param request the buy now request
     * @return the checkout response
     */
    CheckOutResponse buyNow(Long productId, BuyNowRequest request);

    /**
     * Adds an existing service to a product by ID
     * @param productId The ID of the product
     * @param ourServiceId The ID of the service to add
     * @return The updated product response
     */
    ProductResponse addServiceToProduct(Long productId, Long ourServiceId);

    /**
     * Removes a service from a product
     * @param productId The ID of the product
     * @param ourServiceId The ID of the service to remove
     * @return The updated product response
     */
    ProductResponse removeServiceFromProduct(Long productId, Long ourServiceId);

    List<ProductResponse> searchByProductName(String query);
    List<ProductResponse> searchByProductName1(String query);
}
