package com.tradeops.controller;

import com.tradeops.models.dto.DeliveryTaskDTO;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @GetMapping("/couriers/{id}/tasks")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<List<DeliveryTaskDTO>> getMyTasks(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(courierService.deliveryTasks(id));
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

    @PostMapping("/tasks/{taskId}/evidence")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<Void> evidence(@PathVariable Long taskId, String evidenceUrl){
        courierService.setEvidence(taskId, evidenceUrl);
        return ResponseEntity.ok().build();
    }
}
