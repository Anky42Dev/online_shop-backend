package com.shop.onlineshop.models.response;

import com.shop.onlineshop.models.dto.CartItemDto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse (
        List<CartItemDto> items,
        BigDecimal totalPrice
) {
}
