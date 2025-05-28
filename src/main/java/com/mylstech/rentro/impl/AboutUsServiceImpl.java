package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.AboutUsRequest;
import com.mylstech.rentro.dto.response.AboutUsResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.AboutUs;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.repository.AboutUsRepository;
import com.mylstech.rentro.repository.ImageRepository;
import com.mylstech.rentro.service.AboutUsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AboutUsServiceImpl implements AboutUsService {
    private static final Logger logger = LoggerFactory.getLogger ( AboutUsServiceImpl.class );
    private static final String ABOUT_US = "AboutUs";
    private static final String IMAGE = "Image";

    private final AboutUsRepository aboutUsRepository;
    private final ImageRepository imageRepository;

    @Override
    public List<AboutUsResponse> getAllAboutUs() {
        return aboutUsRepository.findAll ( ).stream ( ).map ( AboutUsResponse::new ).toList ( );
    }

    @Override
    public AboutUsResponse getAboutUsById(Long id) {
        AboutUs aboutUs = aboutUsRepository.findById ( id ).orElseThrow ( () -> new ResourceNotFoundException ( ABOUT_US, "id", id ) );
        return new AboutUsResponse ( aboutUs );
    }

    @Override
    @Transactional
    public AboutUsResponse createAboutUs(AboutUsRequest request) {

        logger.info ( "Creating new about us entry with title: {}", request.getTitle ( ) );
        AboutUs aboutUs = request.requestToAboutUs ( );

        // Set image if imageId is provided
        if ( request.getImageId ( ) != null ) {
            Image image = imageRepository.findById ( request.getImageId ( ) ).orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", request.getImageId ( ) ) );
            aboutUs.setImage ( image );


        }

        AboutUs savedAboutUs = aboutUsRepository.save ( aboutUs );
        logger.info ( "Successfully created about us entry with id: {}", savedAboutUs.getAboutUsId ( ) );

        return new AboutUsResponse ( savedAboutUs );

    }

    @Override
    @Transactional
    public AboutUsResponse updateAboutUs(Long id, AboutUsRequest request) {
        AboutUs aboutUs = aboutUsRepository.findById ( id ).orElseThrow ( () -> new ResourceNotFoundException ( ABOUT_US, "id", id ) );

        if ( request.getTitle ( ) != null ) {
            aboutUs.setTitle ( request.getTitle ( ) );
        }

        if ( request.getSubtitle ( ) != null ) {
            aboutUs.setSubtitle ( request.getSubtitle ( ) );
        }

        if ( request.getDescription ( ) != null ) {
            aboutUs.setDescription ( request.getDescription ( ) );
        }

        // Update image if imageId is provided
        if ( request.getImageId ( ) != null ) {
            Image image = imageRepository.findById ( request.getImageId ( ) ).orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", request.getImageId ( ) ) );
            aboutUs.setImage ( image );

        }

        return new AboutUsResponse ( aboutUsRepository.save ( aboutUs ) );
    }

    @Override
    @Transactional
    public void deleteAboutUs(Long id) {
        AboutUs aboutUs = aboutUsRepository.findById ( id ).orElseThrow ( () -> new ResourceNotFoundException ( ABOUT_US, "id", id ) );
        aboutUsRepository.delete ( aboutUs );
    }

    @Override
    @Transactional
    public AboutUsResponse setAboutUsImage(Long aboutUsId, Long imageId) {
        AboutUs aboutUs = aboutUsRepository.findById ( aboutUsId ).orElseThrow ( () -> new ResourceNotFoundException ( ABOUT_US, "id", aboutUsId ) );

        Image image = imageRepository.findById ( imageId ).orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", imageId ) );

        aboutUs.setImage ( image );


        return new AboutUsResponse ( aboutUsRepository.save ( aboutUs ) );
    }

    @Override
    @Transactional
    public AboutUsResponse removeAboutUsImage(Long aboutUsId) {
        AboutUs aboutUs = aboutUsRepository.findById ( aboutUsId ).orElseThrow ( () -> new ResourceNotFoundException ( ABOUT_US, "id", aboutUsId ) );

        aboutUs.setImage ( null );


        return new AboutUsResponse ( aboutUsRepository.save ( aboutUs ) );
    }
}
