package com.shop.onlineshop.models.response;

import com.shop.onlineshop.models.dto.OrderItemDto;
import com.shop.onlineshop.models.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        LocalDateTime createdAt,
        OrderStatus status,
        BigDecimal totalAmount,
        String deliveryCity,
        String deliveryAddress,
        List<OrderItemDto> items
) {
}
