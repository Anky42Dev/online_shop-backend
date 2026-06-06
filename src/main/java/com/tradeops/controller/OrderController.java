package com.tradeops.controller;

import com.tradeops.models.request.PlaceOrderRequest;
import com.tradeops.models.response.OrderResponse;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.service.CustomerOrderService;
import com.tradeops.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CustomerOrderService orderService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.status(201).body(orderService.placeOrder(user, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(orderService.getMyOrders(user));
    }
}