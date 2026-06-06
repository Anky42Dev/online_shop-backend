package com.tradeops.models.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TraderAnalyticsSummary(
        BigDecimal totalRevenue,
        Long totalOrders,
        BigDecimal averageOrderValue,
        LocalDateTime periodFrom,
        LocalDateTime periodTo
) {}