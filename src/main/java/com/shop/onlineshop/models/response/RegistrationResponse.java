package com.shop.onlineshop.models.response;

public record RegistrationResponse(
        String message,
        User user,
        String accessToken,
        String refreshToken,
        boolean isOtpRequired,
        Integer otpExpirationInSeconds
) {
    public record User(
            Long id,
            String fullName,
            String email
    ) {}
}

