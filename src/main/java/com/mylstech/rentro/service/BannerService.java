package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.BannerRequest;
import com.mylstech.rentro.dto.response.BannerResponse;

import java.util.List;

public interface BannerService {
    List<BannerResponse> getAllBanners();
    BannerResponse getBannerById(Long bannerId);
    BannerResponse createBanner(BannerRequest request);
    BannerResponse updateBanner(Long bannerId, BannerRequest request);
    void deleteBanner(Long bannerId);
}
