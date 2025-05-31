package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.*;
import com.mylstech.rentro.dto.response.AuthResponse;
import com.mylstech.rentro.exception.EmailNotVerifiedException;
import com.mylstech.rentro.exception.InvalidOtpException;
import com.mylstech.rentro.exception.PermissionDeniedException;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.RefreshToken;
import com.mylstech.rentro.repository.AppUserRepository;
import com.mylstech.rentro.repository.RefreshTokenRepository;
import com.mylstech.rentro.security.JwtUtil;
import com.mylstech.rentro.service.OtpService;
import com.mylstech.rentro.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String USER_NOT_FOUND = "User not found";
    private final OtpService otpService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    public String register(RegisterRequest request) {
        AppUser user = new AppUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.CUSTOMER);
        user.setVerified(false);

        AppUser saveUser = appUserRepository.save ( user );

        otpService.generateOTPViaEmail (saveUser.getEmail());
         return "Otp sent to "+saveUser.getEmail ();

    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        AppUser user=appUserRepository.findByEmail ( request.getEmail () ).orElseThrow ( ()->new RuntimeException ( "email not found" ) );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        // Check if user is verified
        if (!user.isVerified()) {
            throw new EmailNotVerifiedException ("Account not verified. Please verify your email first.");
        }


        RefreshToken refreshToken;
        // Create and store refresh token
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            // Update existing token
            refreshToken = existingToken.get();
            refreshToken.setToken( UUID.randomUUID().toString());
            refreshToken.setExpiryDate( Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setRevoked(false);
            refreshToken = refreshTokenRepository.save(refreshToken);
        } else {
            // Create new refresh token
            refreshToken = refreshTokenService.createRefreshToken(user);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken ())
                .build();
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        // Find and validate the refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        // Verify token is not expired
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        AppUser user = refreshToken.getUser();
        
        // Generate new access token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        

        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthResponse registerAdmin(RegisterRequest request, String requestingAdminEmail) {
        // Verify the requesting user is an admin
        AppUser requestingAdmin = appUserRepository.findByEmail(requestingAdminEmail)
                .orElseThrow(() -> new ResourceNotFoundException ("Requesting admin not found"));

        if (!Role.ADMIN.equals(requestingAdmin.getRole())) {
            throw new PermissionDeniedException ("Only admins can add other admins");
        }

        // Check if user with this email already exists
        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new PermissionDeniedException ("User with this email already exists");
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

    public void initiateAuthenticationWithEmail(EmailAuthRequest request) {
        // Check if user exists
        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException ( USER_NOT_FOUND ));
        
        // Generate and send OTP
        otpService.generateOTPViaEmail (user.getEmail());
    }

    public AuthResponse completeAuthentication(OtpVerificationRequest request) {
        // Verify OTP
        boolean isValid = otpService.verifyOTP(request.getEmail(), request.getOtp());
        
        if (!isValid) {
            throw new InvalidOtpException ("Invalid OTP or OTP expired");
        }
        
        // Get user
        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException( USER_NOT_FOUND ));
        
        // Check if user is verified (for registration verification)
        if (!user.isVerified()) {
            user.setVerified(true);
            appUserRepository.save(user);
        }
        
        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshToken refreshToken;
        // Create and store refresh token
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            // Update existing token
            refreshToken = existingToken.get();
            refreshToken.setToken( UUID.randomUUID().toString());
            refreshToken.setExpiryDate( Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setRevoked(false);
            refreshToken = refreshTokenRepository.save(refreshToken);
        } else {
            // Create new refresh token
            refreshToken = refreshTokenService.createRefreshToken(user);
        }


        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    public void initiateAuthenticationWithPhoneNo(PhoneAuthRequest request) {
        AppUser user = appUserRepository.findByPhone(request.getPhoneNo ())
                .orElseThrow(() -> new RuntimeException( USER_NOT_FOUND ));

        // Generate and send OTP
        otpService.generateOTPViaPhoneNo (user.getPhone ());
    }
}
