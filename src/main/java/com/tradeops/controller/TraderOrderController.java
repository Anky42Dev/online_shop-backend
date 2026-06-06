package com.tradeops.controller;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.response.TraderOrderResponse;
import com.tradeops.service.TraderOrderService;
import com.tradeops.service.UserService;
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
public class TraderOrderController {

    private final TraderOrderService traderOrderService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<TraderOrderResponse>> getMyOrders(
            @RequestParam(required = false) OrderStatus status) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderOrderService.getMyOrders(trader, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TraderOrderResponse> getMyOrderById(@PathVariable Long id) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderOrderService.getMyOrderById(trader, id));
    }
}