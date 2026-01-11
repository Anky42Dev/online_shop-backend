package com.shop.onlineshop.models.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String role,
        Long userId,
        boolean isOtpRequired,
        Integer otpExpiresInSeconds
) {
}
