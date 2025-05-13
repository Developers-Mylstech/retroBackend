package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.OurServiceRequest;
import com.mylstech.rentro.dto.response.OurServiceResponse;
import com.mylstech.rentro.dto.response.OurServiceWithProductsResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.service.OurServiceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/our-services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OurServiceController {

    private final OurServiceService ourServiceService;
    private final Logger logger = LoggerFactory.getLogger(OurServiceController.class);

    @GetMapping
    public ResponseEntity<List<OurServiceResponse>> getAllOurServices() {
        try {
            logger.debug("Fetching all our services");
            List<OurServiceResponse> ourServices = ourServiceService.getAllOurServices();
            logger.debug("Found {} our services", ourServices.size());
            return ResponseEntity.ok(ourServices);
        } catch (Exception e) {
            logger.error("Error fetching all our services", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OurServiceResponse> getOurServiceById(@PathVariable Long id) {
        try {
            logger.debug("Fetching our service with id: {}", id);
            OurServiceResponse ourService = ourServiceService.getOurServiceById(id);
            logger.debug("Found our service: {}", ourService);
            return ResponseEntity.ok(ourService);
        } catch (Exception e) {
            logger.error("Error fetching our service with id: " + id, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<OurServiceResponse> createOurService(@RequestBody OurServiceRequest request) {
        try {
            logger.debug("Creating new our service: {}", request);
            OurServiceResponse ourService = ourServiceService.createOurService(request);
            logger.debug("Created our service: {}", ourService);
            return new ResponseEntity<>(ourService, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating our service", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OurServiceResponse> updateOurService(@PathVariable Long id, @RequestBody OurServiceRequest request) {
        try {
            OurServiceResponse response = ourServiceService.updateOurService(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOurService(@PathVariable Long id) {
        try {
            logger.debug("Deleting our service with id: {}", id);
            ourServiceService.deleteOurService(id);
            logger.debug("Deleted our service with id: {}", id);
            return ResponseEntity.ok("Our service deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting our service with id: " + id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<OurServiceWithProductsResponse> getOurServiceWithProducts(@PathVariable Long id) {
        try {
            logger.debug("Fetching our service with products for service id: {}", id);
            OurServiceWithProductsResponse response = ourServiceService.getOurServiceWithProducts(id);
            logger.debug("Found our service with {} related products", 
                        response.getRelatedProducts() != null ? response.getRelatedProducts().size() : 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching our service with products for id: " + id, e);
            throw e;
        }
    }

    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set image for a service", 
               description = "Associates an existing image with a service")
    public ResponseEntity<OurServiceResponse> setServiceImage(
            @PathVariable("id") Long serviceId,
            @PathVariable("imageId") Long imageId) {
        try {
            logger.debug("Setting image {} for service {}", imageId, serviceId);
            OurServiceResponse response = ourServiceService.setOurServiceImage(serviceId, imageId);
            logger.debug("Successfully set image for service");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error setting image {} for service {}", imageId, serviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove image from a service",
               description = "Removes the associated image from a service")
    public ResponseEntity<OurServiceResponse> removeServiceImage(
            @PathVariable("id") Long serviceId) {
        try {
            logger.debug("Removing image from service {}", serviceId);
            OurServiceResponse response = ourServiceService.removeOurServiceImage(serviceId);
            logger.debug("Successfully removed image from service");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error removing image from service {}", serviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Unexpected error in OurServiceController", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}
