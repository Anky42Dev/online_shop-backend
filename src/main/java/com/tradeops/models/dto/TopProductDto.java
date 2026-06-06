package com.tradeops.models.dto;

import java.math.BigDecimal;

public record TopProductDto(
        Long productId,
        String productName,
        Long unitsSold,
        BigDecimal revenue
) {}