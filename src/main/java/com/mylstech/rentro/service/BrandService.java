package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.BrandRequest;
import com.mylstech.rentro.dto.response.BrandResponse;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

public interface BrandService {
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(Long id);
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(Long id, BrandRequest brandDetails);
    void deleteBrand(Long id);
    
    /**
     * Get all brand names
     * @return list of brand names
     */
    @Cacheable(value = "brandNames")
    List<String> getAllBrandNames();
    
    /**
     * Set an image for a brand
     * @param brandId the brand ID
     * @param imageId the image ID
     * @return the updated brand response
     */
    @Transactional
    BrandResponse setBrandImage(Long brandId, Long imageId);
    
    /**
     * Remove the image from a brand
     * @param brandId the brand ID
     * @return the updated brand response
     */
    @Transactional
    BrandResponse removeBrandImage(Long brandId);
}
