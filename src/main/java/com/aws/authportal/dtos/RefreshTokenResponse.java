package com.aws.authportal.dtos;

import lombok.Getter;

import java.time.Instant;
import java.util.Date;

public record RefreshTokenResponse (
        String accessToken,
        Date accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {}
