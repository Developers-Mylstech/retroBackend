package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;
import com.mylstech.rentro.model.Feature;
import com.mylstech.rentro.model.OurService;
import com.mylstech.rentro.repository.OurServiceRepository;
import com.mylstech.rentro.service.OurServiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OurServiceServiceImpl implements OurServiceService {

    private final OurServiceRepository ourServiceRepository;
    private final Logger logger = LoggerFactory.getLogger(OurServiceServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<OurServiceResponse> getAllOurServices() {
        try {
            logger.info("Retrieving all our services");
            List<OurService> ourServices = ourServiceRepository.findAll();
            logger.debug("Found {} our services in database", ourServices.size());
            
            List<OurServiceResponse> ourServiceResponses = ourServices.stream()
                    .map(OurServiceResponse::new)
                    .collect(Collectors.toList());
            
            logger.info("Successfully retrieved {} our services", ourServiceResponses.size());
            return ourServiceResponses;
        } catch (Exception e) {
            logger.error("Error retrieving our services from database", e);
            throw new RuntimeException("Failed to retrieve our services", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OurServiceResponse getOurServiceById(Long id) {
        try {
            logger.info("Retrieving our service with id: {}", id);
            OurService ourService = ourServiceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Our service not found with id: " + id));
            
            logger.debug("Found our service with id {}: {}", id, ourService);
            OurServiceResponse response = new OurServiceResponse(ourService);
            logger.info("Successfully retrieved our service with id: {}", id);
            
            return response;
        } catch (Exception e) {
            logger.error("Error retrieving our service with id: {}", id, e);
            throw new RuntimeException("Failed to retrieve our service with id: " + id, e);
        }
    }

    @Override
    @Transactional
    public OurServiceResponse createOurService(OurServiceRequest request) {
        try {
            logger.info("Creating new our service with title: {}", request.getTitle());
            OurService ourService = request.requestToOurService();
            
            OurService savedOurService = ourServiceRepository.save(ourService);
            logger.info("Successfully created our service with id: {}", savedOurService.getOurServiceId());
            
            return new OurServiceResponse(savedOurService);
        } catch (Exception e) {
            logger.error("Error creating our service: {}", request.getTitle(), e);
            throw new RuntimeException("Failed to create our service", e);
        }
    }

    @Override
    @Transactional
    public OurServiceResponse updateOurService(Long id, OurServiceRequest request) {
        try {
            logger.info("Updating our service with id: {}", id);
            OurService ourService = ourServiceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Our service not found with id: " + id));
            
            if (request.getTitle() != null) {
                ourService.setTitle(request.getTitle());
                logger.debug("Updated title to: {} for our service with id: {}", request.getTitle(), id);
            }
            
            if (request.getShortDescription() != null) {
                ourService.setShortDescription(request.getShortDescription());
                logger.debug("Updated short description for our service with id: {}", id);
            }
            
            if (request.getDetailedHeading() != null) {
                ourService.setDetailedHeading(request.getDetailedHeading());
                logger.debug("Updated detailed heading for our service with id: {}", id);
            }
            
            if (request.getDetailedDescription() != null) {
                ourService.setDetailedDescription(request.getDetailedDescription());
                logger.debug("Updated detailed description for our service with id: {}", id);
            }
            
            if (request.getImageUrl() != null) {
                ourService.setImageUrl(request.getImageUrl());
                logger.debug("Updated image URLs for our service with id: {}", id);
            }
            
            if (request.getFeatures() != null) {
                List<Feature> features = request.getFeatures().stream()
                        .map(featureRequest -> {
                            Feature feature = new Feature();
                            feature.setTitle(featureRequest.getTitle());
                            feature.setDescription(featureRequest.getDescription());
                            return feature;
                        })
                        .collect(Collectors.toList());
                
                ourService.setFeature(features);
                logger.debug("Updated features for our service with id: {}", id);
            }
            
            OurService updatedOurService = ourServiceRepository.save(ourService);
            logger.info("Successfully updated our service with id: {}", id);
            
            return new OurServiceResponse(updatedOurService);
        } catch (Exception e) {
            logger.error("Error updating our service with id: {}", id, e);
            throw new RuntimeException("Failed to update our service with id: " + id, e);
        }
    }

    @Override
    @Transactional
    public void deleteOurService(Long id) {
        try {
            logger.info("Deleting our service with id: {}", id);
            OurService ourService = ourServiceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Our service not found with id: " + id));
            
            ourServiceRepository.delete(ourService);
            logger.info("Successfully deleted our service with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting our service with id: {}", id, e);
            throw new RuntimeException("Failed to delete our service with id: " + id, e);
        }
    }
}
