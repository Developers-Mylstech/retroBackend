package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.BannerRequest;
import com.mylstech.rentro.dto.response.BannerResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BannerService {
    List<BannerResponse> getAllBanners();
    BannerResponse getBannerById(Long bannerId);
    BannerResponse createBanner(BannerRequest request);
    BannerResponse updateBanner(Long bannerId, BannerRequest request);
    void deleteBanner(Long bannerId);
    
    /**
     * Set the image for a banner
     * @param bannerId the banner ID
     * @param imageId the image ID
     * @return the updated banner response
     */
    BannerResponse setBannerImage(Long bannerId, Long imageId);
    
    /**
     * Remove the image from a banner
     * @param bannerId the banner ID
     * @return the updated banner response
     */
    BannerResponse removeBannerImage(Long bannerId);
}
