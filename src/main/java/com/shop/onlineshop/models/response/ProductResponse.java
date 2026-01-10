package com.shop.onlineshop.models.response;


import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        String categoryName,
        String traderName
) {}