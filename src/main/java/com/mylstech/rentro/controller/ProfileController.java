package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.ProfileUpdateRequest;
import com.mylstech.rentro.dto.response.ProfileResponse;
import com.mylstech.rentro.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<ProfileResponse> getCurrentUserProfile() {
        try {
            logger.debug("Fetching current user profile");
            ProfileResponse profile = profileService.getCurrentUserProfile();
            logger.debug("Retrieved profile for user ID: {}", profile.getId());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Error fetching current user profile", e);
            throw e;
        }
    }

    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping
    public ResponseEntity<ProfileResponse> updateCurrentUserProfile(@RequestBody ProfileUpdateRequest request) {
        try {
            logger.debug("Updating current user profile");
            ProfileResponse updatedProfile = profileService.updateCurrentUserProfile(request);
            logger.debug("Updated profile for user ID: {}", updatedProfile.getId());
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Error updating current user profile", e);
            throw e;
        }
    }
}