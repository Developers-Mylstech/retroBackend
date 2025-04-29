package com.mylstech.rentro.controller;

import com.mylstech.rentro.dto.request.AuthRequest;
import com.mylstech.rentro.dto.request.RegisterRequest;
import com.mylstech.rentro.dto.request.EmailAuthRequest;
import com.mylstech.rentro.dto.request.OtpVerificationRequest;
import com.mylstech.rentro.dto.response.AuthResponse;
import com.mylstech.rentro.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Initiate authentication with email")
    @PostMapping("/initiate-auth")
    public ResponseEntity<String> initiateAuthentication(@RequestBody EmailAuthRequest request) {
        authService.initiateAuthentication(request);
        return ResponseEntity.ok().body ("OTP sent to email");
    }

    @Operation(summary = "Complete authentication with OTP")
    @PostMapping("/complete-auth")
    public ResponseEntity<AuthResponse> completeAuthentication(@RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.completeAuthentication(request));
    }

    @Operation(summary = "Authenticate admin with email and password")
    @PostMapping("/admin-login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @Operation(summary = "Register a new admin")
    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody RegisterRequest request, Authentication authentication) {
        // Get the email of the currently authenticated admin
        String requestingAdminEmail = authentication.getName();
        
        // Call the service method to register a new admin
        AuthResponse response = authService.registerAdmin(request, requestingAdminEmail);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }
}
