package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.BannerRequest;
import com.mylstech.rentro.dto.response.BannerResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Banner;
import com.mylstech.rentro.repository.BannerRepository;
import com.mylstech.rentro.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    @Override
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAll ().stream ().map ( BannerResponse::new ).toList ();
    }

    @Override
    public BannerResponse getBannerById(Long bannerId) {
        return new BannerResponse (bannerRepository.findById ( bannerId ).orElseThrow ( ()->new ResourceNotFoundException ( "bannerId not found" )));

    }

    @Override
    public BannerResponse createBanner(BannerRequest request) {
        return new BannerResponse (bannerRepository.save ( request.requestToBanner () ));
    }

    @Override
    public BannerResponse updateBanner(Long bannerId, BannerRequest request) {
        Banner banner =  bannerRepository.findById ( bannerId ).orElseThrow ( () -> new ResourceNotFoundException ( "bannerId not found" ) );
        if (request.getTitle ()!=null){
            banner.setTitle ( request.getTitle ( ) );
        }
         if (request.getImageUrl ()!=null){
            banner.setImageUrl ( request.getImageUrl ( ) );
        }
        return  new BannerResponse ( bannerRepository.save ( banner ));

    }

    @Override
    public void deleteBanner(Long bannerId) {
        Banner banner =  bannerRepository.findById ( bannerId ).orElseThrow ( () -> new ResourceNotFoundException ( "bannerId not found" ) );
        bannerRepository.delete ( banner );
    }
}
