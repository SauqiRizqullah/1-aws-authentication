package com.aws.authportal.dtos;

import lombok.Getter;
import lombok.Setter;

public record RefreshTokenRequest(String refreshToken) {}