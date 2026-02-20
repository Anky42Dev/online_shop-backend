package com.tradeops.models.response;


public record RegisterResponse(String message,
                               UserResponse userResponse,
                               String accessToken,
                               String refreshToken) {}
