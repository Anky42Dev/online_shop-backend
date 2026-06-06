package com.tradeops.controller;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.response.TraderOrderResponse;
import com.tradeops.service.TraderOrderService;
import com.tradeops.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trader/orders")
@RequiredArgsConstructor
@Tag(
        name        = "Trader API",
        description = "Управление товарами, заказами и аналитикой для роли Трейдера"
)
@SecurityRequirement(name = "bearerAuth")
public class TraderOrderController {

    private final TraderOrderService traderOrderService;
    private final UserService userService;

    @GetMapping
    @Operation(
            summary     = "Получить список заказов трейдера",
            description = "Возвращает все заказы, связанные с товарами текущего трейдера. "
                    + "Можно фильтровать по статусу заказа."
    )
    public ResponseEntity<List<TraderOrderResponse>> getMyOrders(
            @Parameter(description = "Фильтр по статусу заказа (опционально)")
            @RequestParam(required = false) OrderStatus status) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderOrderService.getMyOrders(trader, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ трейдера по ID")
    public ResponseEntity<TraderOrderResponse> getMyOrderById(
            @Parameter(description = "ID заказа") @PathVariable Long id) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderOrderService.getMyOrderById(trader, id));
    }
}