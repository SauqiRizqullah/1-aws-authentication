package com.aws.authportal.service;

import com.aws.authportal.dtos.*;
import com.aws.authportal.entity.*;
import com.aws.authportal.repository.TokenRepository;
import com.aws.authportal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;

    public AuthenticationService(
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            TokenRepository tokenRepository
    ) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public User signup(RegisterUserDto input) {
        Optional<User> existing = userRepository.findByEmail(input.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        User user = new User();

        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        Role role = mapRoleInput(input.getRole());
        user.setRole(role);

        return userRepository.save(user);
    }

    private Role mapRoleInput(String raw) {
        if (raw == null || raw.isBlank()) return Role.USER;
        try {
            return Role.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // fallback to USER if unknown
            return Role.USER;
        }
    }

    public TokenResponse authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + input.getEmail()));

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generateRandomToken();
        RefreshToken newRT = new RefreshToken();
        newRT.setToken(refreshToken);
        newRT.setUser(user);
        newRT.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        refreshTokenService.saveRefreshToken(newRT);
        return TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

    }

    public RefreshTokenResponse refresh(String refreshToken) {

        RefreshToken saved = refreshTokenService
                .findByToken(refreshToken);

        if (saved.isRevoked() || saved.isExpired()) {
            throw new RuntimeException("Invalid refresh token");
        }
        refreshTokenService.revokeToken(saved);

        // ambil user
        User user = saved.getUser();

        // generate new access token
        String newAccessToken = jwtService.generateToken(user);

        RefreshToken newRefreshToken = refreshTokenService.createNew(user);

        Date accessExpiry = jwtService.extractExpiration(newAccessToken);



        return new RefreshTokenResponse(newAccessToken, accessExpiry, newRefreshToken.getToken(), newRefreshToken.getExpiryDate());
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserDetails userDetails = this.userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                var authResponse = TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
