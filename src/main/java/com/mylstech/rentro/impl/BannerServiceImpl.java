package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.BannerRequest;
import com.mylstech.rentro.dto.response.BannerResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Banner;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.repository.BannerRepository;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private static final String BANNER_NOT_FOUND = "bannerId not found";
    private static final String IMAGE = "Image";
    private final BannerRepository bannerRepository;
    private final ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger ( BannerServiceImpl.class );

    @Override
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAll ( ).stream ( ).map ( BannerResponse::new ).toList ( );
    }

    @Override
    public BannerResponse getBannerById(Long bannerId) {
        return new BannerResponse ( bannerRepository.findById ( bannerId )
                .orElseThrow ( () -> new ResourceNotFoundException ( BANNER_NOT_FOUND ) ) );
    }

    @Override
    public BannerResponse createBanner(BannerRequest request) {
        Banner banner = request.requestToBanner ( );

        // Handle image if imageId is provided
        if ( request.getImageId ( ) != null ) {
            Image image = imageRepository.findById ( request.getImageId ( ) )
                    .orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", request.getImageId ( ) ) );
            banner.setImage ( image );
        }
        // For backward compatibility, handle imageUrl if provided
        else if ( request.getImageUrl ( ) != null ) {
            // The setImageUrl helper method will create a new Image entity
            banner.setImageUrl ( request.getImageUrl ( ) );
        }

        return new BannerResponse ( bannerRepository.save ( banner ) );
    }

    @Override
    @Transactional
    public BannerResponse updateBanner(Long bannerId, BannerRequest request) {
        Banner banner = bannerRepository.findById ( bannerId )
                .orElseThrow ( () -> new ResourceNotFoundException ( BANNER_NOT_FOUND ) );

        if ( request.getTitle ( ) != null ) {
            banner.setTitle ( request.getTitle ( ) );
        }

        // Handle image if imageId is provided
        if ( request.getImageId ( ) != null ) {
            Image image = imageRepository.findById ( request.getImageId ( ) )
                    .orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", request.getImageId ( ) ) );

            // Clear previous image reference
            banner.setImage ( null );
            bannerRepository.saveAndFlush ( banner );

            // Set new image
            banner.setImage ( image );
        }
        // For backward compatibility, handle imageUrl if provided
        else if ( request.getImageUrl ( ) != null ) {
            // Clear previous image reference
            banner.setImage ( null );
            bannerRepository.saveAndFlush ( banner );

            // Find or create image entity for this URL
            Image image = imageRepository.findByImageUrl ( request.getImageUrl ( ) )
                    .orElseGet ( () -> {
                        Image newImage = new Image ( );
                        newImage.setImageUrl ( request.getImageUrl ( ) );
                        return imageRepository.save ( newImage );
                    } );

            banner.setImage ( image );
        }

        return new BannerResponse ( bannerRepository.save ( banner ) );
    }

    @Override
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerRepository.findById ( bannerId )
                .orElseThrow ( () -> new ResourceNotFoundException ( BANNER_NOT_FOUND ) );
        bannerRepository.delete ( banner );
    }

    @Override
    @Transactional
    public BannerResponse setBannerImage(Long bannerId, Long imageId) {
        logger.debug ( "Setting image {} for banner {}", imageId, bannerId );

        Banner banner = bannerRepository.findById ( bannerId )
                .orElseThrow ( () -> new ResourceNotFoundException ( "Banner", "id", bannerId ) );

        Image image = imageRepository.findById ( imageId )
                .orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", imageId ) );

        logger.debug ( "Found banner: {}, image: {}", banner.getBannerId ( ), image.getImageId ( ) );

        // Clear previous image reference
        Image oldImage = banner.getImage ( );
        if ( oldImage != null ) {
            logger.debug ( "Clearing old image: {}", oldImage.getImageId ( ) );
        }

        banner.setImage ( null );
        bannerRepository.saveAndFlush ( banner );
        logger.debug ( "Cleared old image reference" );

        // Set new image
        banner.setImage ( image );
        Banner savedBanner = bannerRepository.save ( banner );
        logger.debug ( "Set new image and saved banner" );

        return new BannerResponse ( savedBanner );
    }

    @Transactional
    @Override
    public BannerResponse removeBannerImage(Long bannerId) {
        Banner banner = bannerRepository.findById ( bannerId )
                .orElseThrow ( () -> new ResourceNotFoundException ( "Banner", "id", bannerId ) );

        banner.setImage ( null );

        return new BannerResponse ( bannerRepository.save ( banner ) );
    }
}
