package com.mylstech.rentro.impl;

import com.mylstech.rentro.exception.RefreshTokenExpiredException;
import com.mylstech.rentro.exception.RefreshTokenRevokedException;
import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.RefreshToken;
import com.mylstech.rentro.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    public RefreshToken createRefreshToken(AppUser user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenRevokedException ("Refresh token was revoked");
        }
        
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException ("Refresh token was expired");
        }
        
        return token;
    }
    
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        refreshTokenRepository.delete ( refreshToken );
    }
    
    @Transactional
    public void revokeAllUserTokens(AppUser user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }
}