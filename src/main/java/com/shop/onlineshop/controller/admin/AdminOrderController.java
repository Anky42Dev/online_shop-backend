package com.shop.onlineshop.controller.admin;

import com.shop.onlineshop.models.entity.OrderEntity;
import com.shop.onlineshop.models.model.OrderStatus;
import com.shop.onlineshop.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminOrderController {

    private final OrderRepo orderRepo;

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return orderRepo.findAll();
    }

    @PutMapping("/{orderId}/status")
    public OrderEntity updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(status);
        return orderRepo.save(order);
    }
}

