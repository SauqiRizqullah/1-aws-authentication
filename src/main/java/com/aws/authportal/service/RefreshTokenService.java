package com.aws.authportal.service;

import com.aws.authportal.entity.RefreshToken;
import com.aws.authportal.entity.User;
import com.aws.authportal.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(RefreshToken token){
        return refreshTokenRepository.save(token);
    }

    public RefreshToken findByToken(String token){
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    public String generateRandomToken() {
        byte[] randomBytes = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public RefreshToken createNew(User user) {
        String generated = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofDays(7));

        RefreshToken newToken = RefreshToken.builder()
                .token(generated)
                .expiryDate(expiry)
                .user(user)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(newToken);
    }
}
