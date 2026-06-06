package com.tradeops.controller;

import com.tradeops.models.dto.TopProductDto;
import com.tradeops.models.response.TraderAnalyticsSummary;
import com.tradeops.service.TraderAnalyticsService;
import com.tradeops.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name        = "Trader API",
        description = "Управление товарами, заказами и аналитикой для роли Трейдера"
)
@SecurityRequirement(name = "bearerAuth")
public class TraderAnalyticsController {

    private final TraderAnalyticsService analyticsService;
    private final UserService userService;

    @GetMapping("/summary")
    @Operation(
            summary     = "Сводная аналитика трейдера",
            description = "Возвращает суммарную выручку, количество заказов и средний чек "
                    + "за указанный период. По умолчанию — последние 30 дней."
    )
    public ResponseEntity<TraderAnalyticsSummary> getSummary(
            @Parameter(description = "Начало периода (ISO 8601). По умолчанию — 30 дней назад")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Конец периода (ISO 8601). По умолчанию — текущий момент")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        LocalDateTime effectiveTo   = to   != null ? to   : LocalDateTime.now();
        LocalDateTime effectiveFrom = from != null ? from : effectiveTo.minusDays(30);

        Long traderId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getSummary(traderId, effectiveFrom, effectiveTo));
    }

    @GetMapping("/top-products")
    @Operation(
            summary     = "Топ продаваемых товаров",
            description = "Возвращает список товаров, отсортированных по количеству продаж "
                    + "за указанный период. Лимит по умолчанию — 10 позиций."
    )
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @Parameter(description = "Начало периода (ISO 8601). По умолчанию — 30 дней назад")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Конец периода (ISO 8601). По умолчанию — текущий момент")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,

            @Parameter(description = "Максимальное количество позиций в ответе")
            @RequestParam(defaultValue = "10") int limit) {

        LocalDateTime effectiveTo   = to   != null ? to   : LocalDateTime.now();
        LocalDateTime effectiveFrom = from != null ? from : effectiveTo.minusDays(30);

        Long traderId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(analyticsService.getTopProducts(traderId, effectiveFrom, effectiveTo, limit));
    }
}