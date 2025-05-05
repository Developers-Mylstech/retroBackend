package com.mylstech.rentro.util;

import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.repository.AppUserRepository;
import com.mylstech.rentro.security.AppUserSecurityDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final AppUserRepository appUserRepository;

    public SecurityUtils(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Get the currently authenticated user
     * @return the current user
     * @throws RuntimeException if user is not authenticated or not found
     */
    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        // Check what type of principal we have
        if (principal instanceof AppUserSecurityDetails) {
            // If principal is AppUserSecurityDetails, get the wrapped AppUser
            AppUserSecurityDetails userDetails = (AppUserSecurityDetails) principal;
            String email = userDetails.getUsername(); // This returns user.getEmail()
            return appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        } else if (principal instanceof String) {
            // If principal is a String (likely username/email), find the user
            String email = (String) principal;
            return appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        } else {
            // Handle other cases based on your security configuration
            throw new RuntimeException("Unexpected principal type: " + principal.getClass().getName());
        }
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}