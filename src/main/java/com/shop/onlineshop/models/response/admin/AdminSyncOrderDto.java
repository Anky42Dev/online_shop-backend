package com.shop.onlineshop.models.response.admin;

import com.shop.onlineshop.models.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminSyncOrderDto(
        Long sourceId,
        String customerEmail,
        List<AdminSyncOrderItemDto> items,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        String city,
        String address,
        String version
) {}

