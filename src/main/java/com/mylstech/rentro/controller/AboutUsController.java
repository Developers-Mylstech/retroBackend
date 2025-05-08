package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.AboutUsRequest;
import com.mylstech.rentro.dto.response.AboutUsResponse;
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
            throw e;
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
            throw e;
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
            throw e;
        }
    }
}