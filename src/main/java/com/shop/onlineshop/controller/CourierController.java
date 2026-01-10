package com.shop.onlineshop.controller;

import com.shop.onlineshop.models.dto.DeliveryTaskDTO;
import com.shop.onlineshop.models.model.OrderStatus;
import com.shop.onlineshop.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<List<DeliveryTaskDTO>> getMyTasks() {
        return ResponseEntity.ok(courierService.deliveryTasks());
    }

    @PostMapping("/tasks/{taskId}/accept")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<Void> acceptTask(@PathVariable Long taskId) {
        courierService.acceptTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tasks/{taskId}/status")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long taskId,
            @RequestParam OrderStatus status
    ) {
        courierService.setStatus(taskId, status);
        return ResponseEntity.ok().build();
    }
}
