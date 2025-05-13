package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.FeatureRequest;
import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;
import com.mylstech.rentro.dto.response.OurServiceWithProductsResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.Feature;
import com.mylstech.rentro.model.Image;
import com.mylstech.rentro.model.OurService;
import com.mylstech.rentro.model.Product;
import com.mylstech.rentro.repository.ImageRepository;
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
    private final ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger(OurServiceServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<OurServiceResponse> getAllOurServices() {
        try {
            logger.info("Retrieving all our services");
            List<OurService> ourServices = ourServiceRepository.findAll();
            logger.debug("Found {} our services in database", ourServices.size());

            List<OurServiceResponse> ourServiceResponses = new ArrayList<>();
            
            for (OurService service : ourServices) {
                try {
                    ourServiceResponses.add(new OurServiceResponse(service));
                } catch (Exception e) {
                    logger.warn("Error converting service with ID {} to response: {}", 
                        service.getOurServiceId(), e.getMessage());
                    // Continue processing other services
                }
            }

            logger.info("Successfully retrieved {} our services", ourServiceResponses.size());
            return ourServiceResponses;
        }
        catch (Exception e) {
            logger.error("Error retrieving our services from database", e);
            throw new RuntimeException("Failed to retrieve our services", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OurServiceResponse getOurServiceById(Long id) {
        try {
            logger.info ( "Retrieving our service with id: {}", id );
            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new RuntimeException ( "Our service not found with id: " + id ) );

            logger.debug ( "Found our service with id {}: {}", id, ourService );
            OurServiceResponse response = new OurServiceResponse ( ourService );
            logger.info ( "Successfully retrieved our service with id: {}", id );

            return response;
        }
        catch ( Exception e ) {
            logger.error ( "Error retrieving our service with id: {}", id, e );
            throw new RuntimeException ( "Failed to retrieve our service with id: " + id, e );
        }
    }

    @Override
    @Transactional
    public OurServiceResponse createOurService(OurServiceRequest request) {
        try {
            logger.info ( "Creating new our service with title: {}", request.getTitle ( ) );
            OurService ourService = request.requestToOurService ( );

            // Handle image if imageId is provided
            if (request.getImageId() != null) {
                Image image = imageRepository.findById(request.getImageId())
                        .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));
                ourService.setImageUrl (image.getImageUrl ());
            }
            // For backward compatibility, handle imageUrl if provided
            else if (request.getImageUrl () != null) {
                // Find or create an Image entity for this URL
                Image image = imageRepository.findByImageUrl(request.getImageUrl ())
                        .orElseGet(() -> {
                            Image newImage = new Image();
                            newImage.setImageUrl(request.getImageUrl ());
                            return imageRepository.save(newImage);
                        });
                ourService.setImage (image);

                // Also set the deprecated field for backward compatibility
                ourService.setImageUrl(request.getImageUrl ());
            }
            OurService savedOurService = ourServiceRepository.save ( ourService );
            logger.info ( "Successfully created our service with id: {}", savedOurService.getOurServiceId ( ) );

            return new OurServiceResponse ( savedOurService );
        }
        catch ( Exception e ) {
            logger.error ( "Error creating our service: {}", request.getTitle ( ), e );
            throw new RuntimeException ( "Failed to create our service", e );
        }
    }

    @Override
    @Transactional
    public OurServiceResponse updateOurService(Long id, OurServiceRequest request) {
        try {
            logger.info ( "Updating our service with id: {}", id );
            logger.debug ( "Request details: {}", request );

            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new RuntimeException ( "Our service not found with id: " + id ) );

            logger.debug ( "Current service before update: {}", ourService );

            if ( request.getTitle ( ) != null ) {
                ourService.setTitle ( request.getTitle ( ) );
                logger.debug ( "Updated title to: {} for our service with id: {}", request.getTitle ( ), id );
            }

            if ( request.getShortDescription ( ) != null ) {
                ourService.setShortDescription ( request.getShortDescription ( ) );
                logger.debug ( "Updated short description for our service with id: {}", id );
            }

            if ( request.getDetailedHeading ( ) != null ) {
                ourService.setDetailedHeading ( request.getDetailedHeading ( ) );
                logger.debug ( "Updated detailed heading for our service with id: {}", id );
            }

            if ( request.getDetailedDescription ( ) != null ) {
                ourService.setDetailedDescription ( request.getDetailedDescription ( ) );
                logger.debug ( "Updated detailed description for our service with id: {}", id );
            }

            // Handle image if imageId is provided
            if (request.getImageId() != null) {
                Image image = imageRepository.findById(request.getImageId())
                        .orElseThrow(() -> new ResourceNotFoundException("Image", "id", request.getImageId()));

                // Clear previous image reference
                ourService.setImageUrl (null);
                ourServiceRepository.saveAndFlush(ourService);

                // Set new image
                ourService.setImage (image);

                // Also update the deprecated field for backward compatibility
                ourService.setImageUrl(image.getImageUrl());
            }
            // For backward compatibility, handle imageUrl if provided
            else if (request.getImageUrl () != null) {
                // Clear previous image reference
                ourService.setImageUrl (null);
                ourServiceRepository.saveAndFlush(ourService);

                // Find or create an Image entity for this URL
                Image image = imageRepository.findByImageUrl(request.getImageUrl ())
                        .orElseGet(() -> {
                            Image newImage = new Image();
                            newImage.setImageUrl(request.getImageUrl ());
                            return imageRepository.save(newImage);
                        });

                ourService.setImage (image);

                // Also set the deprecated field for backward compatibility
                ourService.setImageUrl(request.getImageUrl ());
            }
            if ( request.getFeatures ( ) != null ) {
                // Create a new list for features instead of modifying the existing one
                List<Feature> features = new ArrayList<> ( );

                for (FeatureRequest featureRequest : request.getFeatures ( )) {
                    Feature feature = new Feature ( );
                    feature.setTitle ( featureRequest.getTitle ( ) );
                    feature.setDescription ( featureRequest.getDescription ( ) );
                    features.add ( feature );
                }

                // Clear existing features first to avoid the immutable collection issue
                if ( ourService.getFeature ( ) != null ) {
                    // Create a new list to replace the potentially immutable one
                    ourService.setFeature ( new ArrayList<> ( ourService.getFeature ( ) ) );
                    ourService.getFeature ( ).clear ( );
                    ourServiceRepository.saveAndFlush ( ourService );
                } else {
                    ourService.setFeature ( new ArrayList<> ( ) );
                }
                // Now set the new features
                ourService.setFeature ( features );
                logger.debug ( "Updated features for our service with id: {}", id );
            }

            OurService updatedOurService = ourServiceRepository.save ( ourService );
            logger.debug ( "Service after update: {}", updatedOurService );
            logger.info ( "Successfully updated our service with id: {}", id );

            return new OurServiceResponse ( updatedOurService );
        }
        catch ( Exception e ) {
            logger.error ( "Error updating our service with id: {}", id, e );
            throw new RuntimeException ( "Failed to update our service with id: " + id, e );
        }
    }

    @Override
    @Transactional
    public void deleteOurService(Long id) {
        try {
            logger.info ( "Deleting our service with id: {}", id );
            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new RuntimeException ( "Our service not found with id: " + id ) );

            // First, remove all product associations
            if ( ourService.getProducts ( ) != null && ! ourService.getProducts ( ).isEmpty ( ) ) {
                logger.debug ( "Removing {} product associations from service", ourService.getProducts ( ).size ( ) );

                // Create a copy to avoid ConcurrentModificationException
                List<Product> productsToRemove = new ArrayList<> ( ourService.getProducts ( ) );

                // Remove each product association
                for (Product product : productsToRemove) {
                    ourService.removeProduct ( product );
                }

                // Save the service to update the associations
                ourService = ourServiceRepository.save ( ourService );
            }

            // Now delete the service
            ourServiceRepository.delete ( ourService );
            logger.info ( "Successfully deleted our service with id: {}", id );
        }
        catch ( Exception e ) {
            logger.error ( "Error deleting our service with id: {}", id, e );
            throw new RuntimeException ( "Failed to delete our service with id: " + id, e );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OurServiceWithProductsResponse getOurServiceWithProducts(Long id) {
        try {
            logger.info ( "Retrieving our service with products for service id: {}", id );

            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new RuntimeException ( "Our service not found with id: " + id ) );

            // Ensure products are loaded (if using lazy loading)
            if ( ourService.getProducts ( ) != null ) {
                ourService.getProducts ( ).size ( ); // Force initialization of the collection
                logger.debug ( "Found {} related products for service id: {}",
                        ourService.getProducts ( ).size ( ), id );
            }

            OurServiceWithProductsResponse response = new OurServiceWithProductsResponse ( ourService );
            logger.info ( "Successfully retrieved our service with products for id: {}", id );

            return response;
        }
        catch ( Exception e ) {
            logger.error ( "Error retrieving our service with products for id: {}", id, e );
            throw new RuntimeException ( "Failed to retrieve our service with products for id: " + id, e );
        }
    }

    @Override
    @Transactional
    public OurServiceResponse setOurServiceImage(Long ourServiceId, Long imageId) {
        logger.debug("Setting image {} for our service {}", imageId, ourServiceId);
        
        OurService ourService = ourServiceRepository.findById(ourServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("OurService", "id", ourServiceId));
        
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        logger.debug("Found our service: {}, image: {}", ourService.getOurServiceId(), image.getImageId());
        
        // Clear previous image reference
        Image oldImage = ourService.getImage();
        if (oldImage != null) {
            logger.debug("Clearing old image: {}", oldImage.getImageId());
        }
        
        // Clear the image reference first
        ourService.setImageUrl(null);
        ourServiceRepository.saveAndFlush(ourService);
        logger.debug("Cleared old image reference");
        
        // Check if this image is already used by another service
        List<OurService> servicesWithImage = ourServiceRepository.findByImageImageId(imageId);
        for (OurService existingService : servicesWithImage) {
            if (!existingService.getOurServiceId().equals(ourServiceId)) {
                logger.debug("Image {} is already used by service {}. Creating a copy.", 
                        imageId, existingService.getOurServiceId());
                
                // Create a copy of the image
                Image imageCopy = new Image();
                imageCopy.setImageUrl(image.getImageUrl());
                image = imageRepository.save(imageCopy);
                logger.debug("Created image copy with ID: {}", image.getImageId());
                break;
            }
        }
        
        // Set the new image
        ourService.setImage(image);
        
        // Also update the deprecated field for backward compatibility
        ourService.setImageUrl(image.getImageUrl());
        
        OurService savedService = ourServiceRepository.save(ourService);
        logger.debug("Set new image and saved our service");
        
        return new OurServiceResponse(savedService);
    }
    
    @Override
    @Transactional
    public OurServiceResponse removeOurServiceImage(Long ourServiceId) {
        logger.debug("Removing image from our service {}", ourServiceId);
        
        OurService ourService = ourServiceRepository.findById(ourServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("OurService", "id", ourServiceId));
        
        // Log the current state
        if (ourService.getImage() != null) {
            logger.debug("Current image ID: {}, URL: {}", 
                    ourService.getImage().getImageId(), 
                    ourService.getImage().getImageUrl());
        } else {
            logger.debug("No image currently associated with our service");
        }
        
        try {
            // First approach: Use JPA entity
            ourService.setImage(null);
            ourService.setImageUrl(null);
            
            // Save and flush to ensure changes are committed immediately
            OurService savedService = ourServiceRepository.saveAndFlush(ourService);
            
            // Verify the image was cleared
            if (savedService.getImage() == null) {
                logger.debug("Successfully cleared image from our service using JPA");
            } else {
                logger.warn("Failed to clear image using JPA, trying native query...");
                
                // Second approach: Use native query as fallback
                ourServiceRepository.clearOurServiceImage(ourServiceId);
                
                // Refresh the entity from the database
                ourServiceRepository.flush();
                savedService = ourServiceRepository.findById(ourServiceId)
                        .orElseThrow(() -> new ResourceNotFoundException("OurService", "id", ourServiceId));
                
                if (savedService.getImage() == null) {
                    logger.debug("Successfully cleared image using native query");
                } else {
                    logger.error("Failed to clear image even with native query!");
                }
            }
            
            return new OurServiceResponse(savedService);
        } catch (Exception e) {
            logger.error("Error while removing image from our service", e);
            throw e;
        }
    }
}
