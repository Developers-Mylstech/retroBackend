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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AboutUsServiceImpl implements AboutUsService {
    private static final Logger logger = LoggerFactory.getLogger(AboutUsServiceImpl.class);
    
    private final AboutUsRepository aboutUsRepository;
    private final ImageRepository imageRepository;

    @Override
    public List<AboutUsResponse> getAllAboutUs() {
        return aboutUsRepository.findAll().stream()
                .map(AboutUsResponse::new)
                .toList();
    }

    @Override
    public AboutUsResponse getAboutUsById(Long id) {
        AboutUs aboutUs = aboutUsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AboutUs", "id", id));
        return new AboutUsResponse(aboutUs);
    }

    @Override
    @Transactional
    public AboutUsResponse createAboutUs(AboutUsRequest request) {
        try {
            logger.info("Creating new about us entry with title: {}", request.getTitle());
            AboutUs aboutUs = request.requestToAboutUs();
            
            // Set image if imageId is provided
            if (request.getImageId() != null) {
                Image image = imageRepository.findById(request.getImageId())
                        .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
                aboutUs.setImage(image);
                
                // For backward compatibility, also set imageUrl
                if (aboutUs.getImageUrl() == null) {
                    aboutUs.setImageUrl(image.getImageUrl());
                }
            }
            
            AboutUs savedAboutUs = aboutUsRepository.save(aboutUs);
            logger.info("Successfully created about us entry with id: {}", savedAboutUs.getAboutUsId());
            
            return new AboutUsResponse(savedAboutUs);
        } catch (Exception e) {
            logger.error("Error creating about us entry: {}", request.getTitle(), e);
            throw new RuntimeException("Failed to create about us entry", e);
        }
    }

    @Override
    @Transactional
    public AboutUsResponse updateAboutUs(Long id, AboutUsRequest request) {
        AboutUs aboutUs = aboutUsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AboutUs", "id", id));
        
        if (request.getTitle() != null) {
            aboutUs.setTitle(request.getTitle());
        }
        
        if (request.getSubtitle() != null) {
            aboutUs.setSubtitle(request.getSubtitle());
        }
        
        if (request.getDescription() != null) {
            aboutUs.setDescription(request.getDescription());
        }
        
        // Update image if imageId is provided
        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
            aboutUs.setImage(image);
            
            // For backward compatibility, also update imageUrl
            aboutUs.setImageUrl(image.getImageUrl());
        } 
        // If only imageUrl is provided (backward compatibility)
        else if (request.getImageUrl() != null) {
            aboutUs.setImageUrl(request.getImageUrl());
            
            // Try to find or create an Image entity for this URL
            Image image = imageRepository.findByImageUrl(request.getImageUrl())
                    .orElseGet(() -> {
                        Image newImage = new Image();
                        newImage.setImageUrl(request.getImageUrl());
                        return imageRepository.save(newImage);
                    });
            aboutUs.setImage(image);
        }
        
        return new AboutUsResponse(aboutUsRepository.save(aboutUs));
    }

    @Override
    @Transactional
    public void deleteAboutUs(Long id) {
        AboutUs aboutUs = aboutUsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AboutUs", "id", id));
        aboutUsRepository.delete(aboutUs);
    }
    
    @Override
    @Transactional
    public AboutUsResponse setAboutUsImage(Long aboutUsId, Long imageId) {
        AboutUs aboutUs = aboutUsRepository.findById(aboutUsId)
                .orElseThrow(() -> new ResourceNotFoundException("AboutUs", "id", aboutUsId));
        
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        aboutUs.setImage(image);
        aboutUs.setImageUrl(image.getImageUrl()); // For backward compatibility
        
        return new AboutUsResponse(aboutUsRepository.save(aboutUs));
    }
    
    @Override
    @Transactional
    public AboutUsResponse removeAboutUsImage(Long aboutUsId) {
        AboutUs aboutUs = aboutUsRepository.findById(aboutUsId)
                .orElseThrow(() -> new ResourceNotFoundException("AboutUs", "id", aboutUsId));
        
        aboutUs.setImage(null);
        aboutUs.setImageUrl(null); // Also clear the imageUrl for consistency
        
        return new AboutUsResponse(aboutUsRepository.save(aboutUs));
    }
}
