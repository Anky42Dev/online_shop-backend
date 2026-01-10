package com.shop.onlineshop.models.dto;

import com.shop.onlineshop.models.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record DeliveryTaskDTO(
        OrderStatus orderStatus,
        String city,
        String address,
        List<OrderItemDto> orderItems,
        BigDecimal price

) {}
