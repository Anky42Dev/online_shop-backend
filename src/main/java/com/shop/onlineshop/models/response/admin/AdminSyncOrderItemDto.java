package com.shop.onlineshop.models.response.admin;

import java.math.BigDecimal;

public record AdminSyncOrderItemDto(
        Long productId,
        String productName,
        int quantity,
        BigDecimal priceAtPurchase
) {}

