package com.shop.onlineshop.models.response.admin;

import java.math.BigDecimal;

public record AdminSyncProductDto(
        Long sourceId,
        String title,
        BigDecimal price,
        int centralStock,
        String category,
        String version
) {}

