package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ProfileUpdateRequest;
import com.mylstech.rentro.dto.response.ProfileResponse;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.repository.AppUserRepository;
import com.mylstech.rentro.service.ProfileService;
import com.mylstech.rentro.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final AppUserRepository appUserRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCurrentUserProfile() {
        AppUser currentUser = securityUtils.getCurrentUser();
        logger.debug("Retrieving profile for user: {}", currentUser.getEmail());
        return new ProfileResponse(currentUser);
    }

    @Override
    @Transactional
    public ProfileResponse updateCurrentUserProfile(ProfileUpdateRequest request) {
        AppUser currentUser = securityUtils.getCurrentUser();
        logger.debug("Updating profile for user: {}", currentUser.getEmail());
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            currentUser.setName(request.getName());
            logger.debug("Updated name to: {}", request.getName());
        }
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            currentUser.setPhone(request.getPhone());
            logger.debug("Updated phone to: {}", request.getPhone());
        }
        
        // Add address update
        if (request.getAddress() != null) {
            currentUser.setAddress(request.getAddress());
            logger.debug("Updated address to: {}", request.getAddress());
        }
        
        AppUser updatedUser = appUserRepository.save(currentUser);
        logger.debug("Profile updated successfully for user: {}", updatedUser.getEmail());
        
        return new ProfileResponse(updatedUser);
    }
}