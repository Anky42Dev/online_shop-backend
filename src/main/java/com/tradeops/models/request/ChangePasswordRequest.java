package com.tradeops.models.request;

public record ChangePasswordRequest(String oldPassword,
                                    String newPassword) {}
