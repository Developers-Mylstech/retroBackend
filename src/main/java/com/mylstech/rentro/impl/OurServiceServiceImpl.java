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

@Service
@RequiredArgsConstructor
public class OurServiceServiceImpl implements OurServiceService {

    private static final String OUR_SERVICE = "OurService";
    private static final String IMAGE = "Image";
    private static final String SERVICE_NOT_FOUND_WITH_ID = "Our service not found with id: ";
    private final OurServiceRepository ourServiceRepository;
    private final ImageRepository imageRepository;
    private final Logger logger = LoggerFactory.getLogger ( OurServiceServiceImpl.class );

    @Override
    @Transactional(readOnly = true)
    public List<OurServiceResponse> getAllOurServices() {

            logger.info ( "Retrieving all our services" );
            List<OurService> ourServices = ourServiceRepository.findAll ( );
            logger.debug ( "Found {} our services in database", ourServices.size ( ) );

            List<OurServiceResponse> ourServiceResponses = new ArrayList<> ( );

            for (OurService service : ourServices) {
                try {
                    ourServiceResponses.add ( new OurServiceResponse ( service ) );
                }
                catch ( Exception e ) {
                    logger.warn ( "Error converting service with ID {} to response: {}",
                            service.getOurServiceId ( ), e.getMessage ( ) );
                    // Continue processing other services
                }
            }

            logger.info ( "Successfully retrieved {} our services", ourServiceResponses.size ( ) );
            return ourServiceResponses;

    }

    @Override
    @Transactional(readOnly = true)
    public OurServiceResponse getOurServiceById(Long id) {

            logger.info ( "Retrieving our service with id: {}", id );
            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );

            logger.debug ( "Found our service with id {}: {}", id, ourService );
            OurServiceResponse response = new OurServiceResponse ( ourService );
            logger.info ( "Successfully retrieved our service with id: {}", id );

            return response;

    }

    @Override
    @Transactional
    public OurServiceResponse createOurService(OurServiceRequest request) {

            logger.info ( "Creating new our service with title: {}", request.getTitle ( ) );
            OurService ourService = request.requestToOurService ( );

            // Handle image if imageId is provided
            if ( request.getImageId ( ) != null ) {
                Image image = imageRepository.findById ( request.getImageId ( ) )
                        .orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", request.getImageId ( ) ) );
                ourService.setImage ( image );
            }

            OurService savedOurService = ourServiceRepository.save ( ourService );
            logger.info ( "Successfully created our service with id: {}", savedOurService.getOurServiceId ( ) );

            return new OurServiceResponse ( savedOurService );

    }

    @Override
    @Transactional
    public OurServiceResponse updateOurService(Long id, OurServiceRequest request) {

            logger.info ( "Updating our service with id: {}", id );
            logger.debug ( "Request details: {}", request );

            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );

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
            if ( request.getImageId ( ) != null ) {
                Image image = imageRepository.findById ( request.getImageId ( ) )
                        .orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", request.getImageId ( ) ) );






                ourService.setImage ( image );



            }


//                // Clear previous image reference
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

    @Override
    @Transactional
    public void deleteOurService(Long id) {

            logger.info ( "Deleting our service with id: {}", id );
            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );

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

    @Override
    @Transactional(readOnly = true)
    public OurServiceWithProductsResponse getOurServiceWithProducts(Long id) {

            logger.info ( "Retrieving our service with products for service id: {}", id );

            OurService ourService = ourServiceRepository.findById ( id )
                    .orElseThrow ( () -> new ResourceNotFoundException ( SERVICE_NOT_FOUND_WITH_ID + id ) );

            // Ensure products are loaded (if using lazy loading)
            if ( ourService.getProducts ( ) != null ) {
                int size = ourService.getProducts ( ).size ( );// Force initialization of the collection
                logger.debug ( "Found {} related products for service id: {}",
                        size, id );
            }

            OurServiceWithProductsResponse response = new OurServiceWithProductsResponse ( ourService );
            logger.info ( "Successfully retrieved our service with products for id: {}", id );

            return response;

    }

    @Override
    @Transactional
    public OurServiceResponse setOurServiceImage(Long ourServiceId, Long imageId) {
        logger.debug ( "Setting image {} for our service {}", imageId, ourServiceId );

        OurService ourService = ourServiceRepository.findById ( ourServiceId )
                .orElseThrow ( () -> new ResourceNotFoundException ( OUR_SERVICE, "id", ourServiceId ) );

        Image image = imageRepository.findById ( imageId )
                .orElseThrow ( () -> new ResourceNotFoundException ( IMAGE, "id", imageId ) );

        logger.debug ( "Found our service: {}, image: {}", ourService.getOurServiceId ( ), image.getImageId ( ) );

        // Clear previous image reference
        Image oldImage = ourService.getImage ( );
        if ( oldImage != null ) {
            logger.debug ( "Clearing old image: {}", oldImage.getImageId ( ) );
        }

        // Clear the image reference first

        ourServiceRepository.saveAndFlush ( ourService );
        logger.debug ( "Cleared old image reference" );

        // Check if this image is already used by another service
        List<OurService> servicesWithImage = ourServiceRepository.findByImageImageId ( imageId );
        for (OurService existingService : servicesWithImage) {
            if ( ! existingService.getOurServiceId ( ).equals ( ourServiceId ) ) {
                logger.debug ( "Image {} is already used by service {}. Creating a copy.",
                        imageId, existingService.getOurServiceId ( ) );

                // Create a copy of the image
                Image imageCopy = new Image ( );
                imageCopy.setImageUrl ( image.getImageUrl ( ) );
                image = imageRepository.save ( imageCopy );
                logger.debug ( "Created image copy with ID: {}", image.getImageId ( ) );
                break;
            }
        }

        // Set the new image
        ourService.setImage ( image );

        // Also update the deprecated field for backward compatibility


        OurService savedService = ourServiceRepository.save ( ourService );
        logger.debug ( "Set new image and saved our service" );

        return new OurServiceResponse ( savedService );
    }

    @Override
    @Transactional
    public OurServiceResponse removeOurServiceImage(Long ourServiceId) {
        logger.debug ( "Removing image from our service {}", ourServiceId );

        OurService ourService = ourServiceRepository.findById ( ourServiceId )
                .orElseThrow ( () -> new ResourceNotFoundException ( OUR_SERVICE, "id", ourServiceId ) );

        // Log the current state
        if ( ourService.getImage ( ) != null ) {
            logger.debug ( "Current image ID: {}, URL: {}",
                    ourService.getImage ( ).getImageId ( ),
                    ourService.getImage ( ).getImageUrl ( ) );
        } else {
            logger.debug ( "No image currently associated with our service" );
        }


            // First approach: Use JPA entity
            ourService.setImage ( null );


            // Save and flush to ensure changes are committed immediately
            OurService savedService = ourServiceRepository.saveAndFlush ( ourService );

            // Verify the image was cleared
            if ( savedService.getImage ( ) == null ) {
                logger.debug ( "Successfully cleared image from our service using JPA" );
            } else {
                logger.warn ( "Failed to clear image using JPA, trying native query..." );

                // Second approach: Use native query as fallback
                ourServiceRepository.clearOurServiceImage ( ourServiceId );

                // Refresh the entity from the database
                ourServiceRepository.flush ( );
                savedService = ourServiceRepository.findById ( ourServiceId )
                        .orElseThrow ( () -> new ResourceNotFoundException ( OUR_SERVICE, "id", ourServiceId ) );

                if ( savedService.getImage ( ) == null ) {
                    logger.debug ( "Successfully cleared image using native query" );
                } else {
                    logger.error ( "Failed to clear image even with native query!" );
                }
            }

            return new OurServiceResponse ( savedService );
            }
}
