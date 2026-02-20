package com.tradeops.models.response;

import com.tradeops.models.dto.CartItemDto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse (
        List<CartItemDto> items,
        BigDecimal totalPrice
) {
}
