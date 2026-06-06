package com.tradeops.service;

import com.tradeops.models.dto.TopProductDto;
import com.tradeops.models.response.TraderAnalyticsSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface TraderAnalyticsService {

    TraderAnalyticsSummary getSummary(Long traderId, LocalDateTime from, LocalDateTime to);

    List<TopProductDto> getTopProducts(Long traderId, LocalDateTime from, LocalDateTime to, int limit);
}