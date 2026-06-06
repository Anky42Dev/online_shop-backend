package com.tradeops.controller.admin;

import com.tradeops.models.entity.Courier;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.admin.AdminCreateCourierRequest;
import com.tradeops.models.response.PageResponse;
import com.tradeops.repo.CourierRepo;
import com.tradeops.repo.UserEntityRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/couriers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCourierController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CourierRepo courierRepo;
    private final UserEntityRepo userRepo;

    @PostMapping
    public ResponseEntity<Courier> createCourier(@Valid @RequestBody AdminCreateCourierRequest request) {
        UserEntity user = userRepo.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Courier courier = new Courier();
        courier.setUserEntity(user);

        return ResponseEntity.ok(courierRepo.save(courier));
    }

    @GetMapping
    public ResponseEntity<PageResponse<Courier>> getAllCouriers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (size > MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().build();
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(PageResponse.from(courierRepo.findAll(pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Courier> updateCourier(
            @PathVariable Long id,
            @Valid @RequestBody AdminCreateCourierRequest request
    ) {
        Courier courier = courierRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        UserEntity user = userRepo.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        courier.setUserEntity(user);
        return ResponseEntity.ok(courierRepo.save(courier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourier(@PathVariable Long id) {
        Courier courier = courierRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));
        courierRepo.delete(courier);
        return ResponseEntity.noContent().build();
    }
}