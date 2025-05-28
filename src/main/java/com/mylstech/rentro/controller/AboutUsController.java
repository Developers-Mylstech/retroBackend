package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.AboutUsRequest;
import com.mylstech.rentro.dto.response.AboutUsResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.service.AboutUsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/about-us")
@RequiredArgsConstructor
@Tag(name = "About Us", description = "About Us management APIs")
public class AboutUsController {
    private static final Logger logger = LoggerFactory.getLogger(AboutUsController.class);
    
    private final AboutUsService aboutUsService;
    
    @GetMapping
    @Operation(summary = "Get all about us entries", description = "Returns all about us entries")
    public ResponseEntity<List<AboutUsResponse>> getAllAboutUs() {
        return ResponseEntity.ok(aboutUsService.getAllAboutUs());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get about us by ID", description = "Returns a specific about us entry by its ID")
    public ResponseEntity<AboutUsResponse> getAboutUsById(@PathVariable Long id) {
        return ResponseEntity.ok(aboutUsService.getAboutUsById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create about us", description = "Creates a new about us entry")
    public ResponseEntity<AboutUsResponse> createAboutUs(@Valid @RequestBody AboutUsRequest request) {
        try {
            logger.debug("Creating new about us entry: {}", request);
            AboutUsResponse aboutUs = aboutUsService.createAboutUs(request);
            logger.debug("Created about us entry: {}", aboutUs);
            return new ResponseEntity<>(aboutUs, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating about us entry", e);
            throw new ResourceNotFoundException ( "error creating About us" );
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update about us", description = "Updates an existing about us entry")
    public ResponseEntity<AboutUsResponse> updateAboutUs(
            @PathVariable Long id, 
            @Valid @RequestBody AboutUsRequest request) {
        try {
            logger.debug("Updating about us entry with ID: {}", id);
            AboutUsResponse aboutUs = aboutUsService.updateAboutUs(id, request);
            logger.debug("Updated about us entry: {}", aboutUs);
            return ResponseEntity.ok(aboutUs);
        } catch (Exception e) {
            logger.error("Error updating about us entry with ID: " + id, e);
            throw new ResourceNotFoundException ( "error updating About us", "id", id);
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete about us", description = "Deletes an about us entry")
    public ResponseEntity<Void> deleteAboutUs(@PathVariable Long id) {
        try {
            logger.debug("Deleting about us entry with ID: {}", id);
            aboutUsService.deleteAboutUs(id);
            logger.debug("Deleted about us entry with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting about us entry with ID: " + id, e);
            throw new ResourceNotFoundException ( "error deleting About us", "id", id);
        }
    }
    
    @PutMapping("/{id}/image/{imageId}")
    @Operation(summary = "Set about us image", description = "Sets the image for an about us entry")
    public ResponseEntity<AboutUsResponse> setAboutUsImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        try {
            logger.debug("Setting image with ID {} for about us entry with ID: {}", imageId, id);
            AboutUsResponse aboutUs = aboutUsService.setAboutUsImage(id, imageId);
            logger.debug("Set image for about us entry: {}", aboutUs);
            return ResponseEntity.ok(aboutUs);
        } catch (Exception e) {
            logger.error("Error setting image for about us entry with ID: " + id, e);
            throw new ResourceNotFoundException ( "error setting About us image", "id", imageId);
        }
    }
    
    @DeleteMapping("/{id}/image")
    @Operation(summary = "Remove about us image", description = "Removes the image from an about us entry")
    public ResponseEntity<AboutUsResponse> removeAboutUsImage(@PathVariable Long id) {
        try {
            logger.debug("Removing image from about us entry with ID: {}", id);
            AboutUsResponse aboutUs = aboutUsService.removeAboutUsImage(id);
            logger.debug("Removed image from about us entry: {}", aboutUs);
            return ResponseEntity.ok(aboutUs);
        } catch (Exception e) {
            logger.error("Error removing image from about us entry with ID: " + id, e);
            throw new ResourceNotFoundException ( "error removing About us image as ", "id", id);
        }
    }
}
