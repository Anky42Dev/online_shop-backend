package com.tradeops.controller;

import com.tradeops.models.dto.TopProductDto;
import com.tradeops.models.response.TraderAnalyticsSummary;
import com.tradeops.service.TraderAnalyticsService;
import com.tradeops.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trader/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRADER')")
public class TraderAnalyticsController {

    private final TraderAnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/summary")
    public ResponseEntity<TraderAnalyticsSummary> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        LocalDateTime effectiveTo   = to   != null ? to   : LocalDateTime.now();
        LocalDateTime effectiveFrom = from != null ? from : effectiveTo.minusDays(30);

        Long traderId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getSummary(traderId, effectiveFrom, effectiveTo));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,

            @RequestParam(defaultValue = "10") int limit) {

        LocalDateTime effectiveTo   = to   != null ? to   : LocalDateTime.now();
        LocalDateTime effectiveFrom = from != null ? from : effectiveTo.minusDays(30);

        Long traderId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getTopProducts(traderId, effectiveFrom, effectiveTo, limit));
    }
}