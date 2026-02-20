package com.tradeops.models.dto;

import com.tradeops.models.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record DeliveryTaskDTO(
        OrderStatus orderStatus,
        String city,
        String address,
        List<OrderItemDto> orderItems,
        BigDecimal price

) {}
