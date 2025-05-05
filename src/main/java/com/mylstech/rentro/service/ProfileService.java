package com.mylstech.rentro.service;

import com.mylstech.rentro.dto.request.ProfileUpdateRequest;
import com.mylstech.rentro.dto.response.ProfileResponse;

public interface ProfileService {
    /**
     * Get the profile of the currently authenticated user
     * @return the user's profile
     */
    ProfileResponse getCurrentUserProfile();
    
    /**
     * Update the profile of the currently authenticated user
     * @param request the profile update request
     * @return the updated profile
     */
    ProfileResponse updateCurrentUserProfile(ProfileUpdateRequest request);
}