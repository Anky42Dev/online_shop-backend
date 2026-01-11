package com.shop.onlineshop.models.request.admin;

import jakarta.validation.constraints.NotNull;

public record AdminCreateCourierRequest(
        @NotNull Long userId
) {}

