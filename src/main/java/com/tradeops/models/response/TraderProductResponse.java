package com.tradeops.models.response;

import java.math.BigDecimal;

public record TraderProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String categoryName,
        Long categoryId
) {}