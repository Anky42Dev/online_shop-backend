package com.tradeops.models.request;

public record OtpVerifyRequest(
        String username,
        String otp
) {}
