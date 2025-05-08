package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.AboutUsRequest;
import com.mylstech.rentro.dto.response.AboutUsResponse;

import java.util.List;

public interface AboutUsService {
    List<AboutUsResponse> getAllAboutUs();
    AboutUsResponse getAboutUsById(Long id);
    AboutUsResponse createAboutUs(AboutUsRequest request);
    AboutUsResponse updateAboutUs(Long id, AboutUsRequest request);
    void deleteAboutUs(Long id);
    
    // New methods for image management
    AboutUsResponse setAboutUsImage(Long aboutUsId, Long imageId);
    AboutUsResponse removeAboutUsImage(Long aboutUsId);
}
