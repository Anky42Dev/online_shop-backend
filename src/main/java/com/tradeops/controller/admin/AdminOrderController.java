package com.tradeops.controller.admin;

import com.tradeops.models.entity.OrderEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.response.PageResponse;
import com.tradeops.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminOrderController {

    private static final int MAX_PAGE_SIZE = 100;

    private final OrderRepo orderRepo;

    @GetMapping
    public ResponseEntity<PageResponse<OrderEntity>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (size > MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().build();
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(PageResponse.from(orderRepo.findAll(pageable)));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderEntity> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status
    ) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        return ResponseEntity.ok(orderRepo.save(order));
    }
}