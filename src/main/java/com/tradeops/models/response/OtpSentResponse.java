package com.tradeops.models.response;

public record OtpSentResponse(
        String message,
        int expiresInSeconds
) {}
