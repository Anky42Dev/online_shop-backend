package com.tradeops.models.response;

import com.tradeops.models.dto.OrderItemDto;
import com.tradeops.models.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TraderOrderResponse(
        Long orderId,
        LocalDateTime createdAt,
        OrderStatus status,
        BigDecimal totalAmount,
        String deliveryCity,
        String deliveryAddress,
        String customerFullName,
        String customerEmail,
        List<OrderItemDto> items
) {}