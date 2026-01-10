package com.shop.onlineshop.models.request;

import jakarta.validation.constraints.NotBlank;

public record AdminCategoryRequest(
        @NotBlank
        String name
) {}

