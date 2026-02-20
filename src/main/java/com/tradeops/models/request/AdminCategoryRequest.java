package com.tradeops.models.request;

import jakarta.validation.constraints.NotBlank;

public record AdminCategoryRequest(
        @NotBlank
        String name
) {}

