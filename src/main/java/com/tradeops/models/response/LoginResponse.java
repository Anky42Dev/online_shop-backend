package com.tradeops.models.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String role,
        Long userId
) {
}
