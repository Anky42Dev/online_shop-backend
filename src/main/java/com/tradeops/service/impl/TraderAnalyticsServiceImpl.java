package com.tradeops.service.impl;

import com.tradeops.models.dto.TopProductDto;
import com.tradeops.models.response.TraderAnalyticsSummary;
import com.tradeops.repo.OrderRepo;
import com.tradeops.service.TraderAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TraderAnalyticsServiceImpl implements TraderAnalyticsService {

    private final OrderRepo orderRepo;

    @Override
    @Transactional(readOnly = true)
    public TraderAnalyticsSummary getSummary(Long traderId, LocalDateTime from, LocalDateTime to) {
        BigDecimal totalRevenue = orderRepo.getTotalRevenue(traderId, from, to);
        Long totalOrders = orderRepo.countOrders(traderId, from, to);

        BigDecimal averageOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new TraderAnalyticsSummary(totalRevenue, totalOrders, averageOrderValue, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDto> getTopProducts(Long traderId, LocalDateTime from, LocalDateTime to, int limit) {
        return orderRepo.getTopProducts(traderId, from, to, PageRequest.of(0, limit));
    }
}