package com.shop.onlineshop.controller;

import com.shop.onlineshop.models.request.PlaceOrderRequest;
import com.shop.onlineshop.models.response.OrderResponse;
import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.service.CustomerOrderService;
import com.shop.onlineshop.service.UserService;
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
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.status(201).body(orderService.placeOrder(user, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(orderService.getMyOrders(user));
    }
}