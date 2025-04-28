package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.AppUser;
import com.mylstech.rentro.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUser(AppUser user);
    Optional<RefreshToken> findByUser(AppUser user);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user = :user")
    void revokeAllUserTokens(AppUser user);
}