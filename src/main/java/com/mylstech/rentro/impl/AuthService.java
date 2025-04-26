package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.AuthRequest;
import com.mylstech.rentro.dto.request.EmailAuthRequest;
import com.mylstech.rentro.dto.request.OtpVerificationRequest;
import com.mylstech.rentro.dto.request.RegisterRequest;
import com.mylstech.rentro.dto.response.AuthResponse;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.RefreshToken;
import com.mylstech.rentro.repository.AppUserRepository;
import com.mylstech.rentro.repository.RefreshTokenRepository;
import com.mylstech.rentro.security.JwtUtil;
import com.mylstech.rentro.service.OtpService;
import com.mylstech.rentro.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final OtpService otpService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse register(RegisterRequest request) {
        AppUser user = new AppUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.CUSTOMER);
        user.setVerified(false);
        
        appUserRepository.save(user);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        // Find and validate the refresh token
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(
                refreshTokenRepository.findByToken(refreshTokenStr)
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"))
        );
        
        AppUser user = refreshToken.getUser();
        
        // Generate new access token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        
        // Optional: Implement token rotation by revoking the old token and creating a new one
        refreshTokenService.revokeToken(refreshTokenStr);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public AuthResponse registerAdmin(RegisterRequest request, String requestingAdminEmail) {
        // Verify the requesting user is an admin
        AppUser requestingAdmin = appUserRepository.findByEmail(requestingAdminEmail)
                .orElseThrow(() -> new RuntimeException("Requesting admin not found"));

        if (!Role.ADMIN.equals(requestingAdmin.getRole())) {
            throw new RuntimeException("Only admins can add other admins");
        }

        // Check if user with this email already exists
        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create the new admin user
        AppUser newAdmin = new AppUser();
        newAdmin.setName(request.getName());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setPhone(request.getPhone());
        newAdmin.setRole( Role.ADMIN); // Explicitly set role to ADMIN
        newAdmin.setVerified(true); // Admins are auto-verified

        appUserRepository.save(newAdmin);

        // Generate tokens for the new admin
        UserDetails userDetails = userDetailsService.loadUserByUsername(newAdmin.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void initiateAuthentication(EmailAuthRequest request) {
        // Check if user exists
        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate and send OTP
        otpService.generateOTP(user.getEmail());
    }

    public AuthResponse completeAuthentication(OtpVerificationRequest request) {
        // Verify OTP
        boolean isValid = otpService.verifyOTP(request.getEmail(), request.getOtp());
        
        if (!isValid) {
            throw new RuntimeException("Invalid OTP or OTP expired");
        }
        
        // Get user
        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is verified (for registration verification)
        if (!user.isVerified()) {
            user.setVerified(true);
            appUserRepository.save(user);
        }
        
        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        
        // Create and store refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }
}
