package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.BrandRequest;
import com.mylstech.rentro.dto.response.BrandResponse;
import com.mylstech.rentro.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.exception.UniqueConstraintViolationException;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Brand Management", description = "API endpoints for managing brands")
public class BrandController {

    private final BrandService brandService;
    private final Logger logger = LoggerFactory.getLogger(BrandController.class);

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok ( brandService.getAllBrands ( ) );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok ( brandService.getBrandById ( id ) );
    }

    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@RequestBody BrandRequest request) {
        return new ResponseEntity<> ( brandService.createBrand ( request ), HttpStatus.CREATED );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
       return ResponseEntity.ok ( brandService.updateBrand ( id, request ) );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand ( id );
        return ResponseEntity.noContent ( ).build ( );
    }

    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set image for a brand", 
               description = "Associates an existing image with a brand")
    public ResponseEntity<BrandResponse> setBrandImage(
            @PathVariable("id") Long brandId,
            @PathVariable("imageId") Long imageId) {
        try {
            logger.debug("Setting image {} for brand {}", imageId, brandId);
            BrandResponse response = brandService.setBrandImage(brandId, imageId);
            logger.debug("Successfully set image for brand");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error setting image {} for brand {}", imageId, brandId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove image from a brand", 
               description = "Removes the image from a brand")
    public ResponseEntity<BrandResponse> removeBrandImage(
            @PathVariable("id") Long brandId) {
        try {
            logger.debug("Removing image from brand {}", brandId);
            BrandResponse response = brandService.removeBrandImage(brandId);
            logger.debug("Successfully removed image from brand");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error removing image from brand {}", brandId, e);
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
        logger.error("Unexpected error in BrandController", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
    
    @ExceptionHandler(UniqueConstraintViolationException.class)
    public ResponseEntity<String> handleUniqueConstraintViolationException(UniqueConstraintViolationException ex) {
        logger.error("Unique constraint violation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
    
    @GetMapping("/names")
    @Operation(summary = "Get all brand names", 
               description = "Returns a list of all brand names")
    public ResponseEntity<List<String>> getAllBrandNames() {
        try {
            logger.debug("Fetching all brand names");
            List<String> brandNames = brandService.getAllBrandNames();
            logger.debug("Found {} brand names", brandNames.size());
            return ResponseEntity.ok(brandNames);
        } catch (Exception e) {
            logger.error("Error fetching brand names", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
