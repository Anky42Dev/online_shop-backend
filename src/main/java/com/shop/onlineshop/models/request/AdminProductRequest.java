package com.shop.onlineshop.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdminProductRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        BigDecimal price,

        @Min(0)
        int stockQuantity,

        @NotNull
        Long categoryId
) {}

