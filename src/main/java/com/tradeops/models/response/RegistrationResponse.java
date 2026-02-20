package com.tradeops.models.response;

public record RegistrationResponse(
        String message,
        User user,
        String accessToken,
        String refreshToken
) {
    public record User(
            Long id,
            String fullName,
            String email
    ) {}
}

