package com.tradeops.controller.admin;

import com.tradeops.models.entity.Courier;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.admin.AdminCreateCourierRequest;
import com.tradeops.repo.CourierRepo;
import com.tradeops.repo.UserEntityRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/couriers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCourierController {

    private final CourierRepo courierRepo;
    private final UserEntityRepo userRepo;

    @PostMapping
    public Courier createCourier(@Valid @RequestBody AdminCreateCourierRequest request) {
        UserEntity user = userRepo.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Courier courier = new Courier();
        courier.setUserEntity(user);

        return courierRepo.save(courier);
    }

    @GetMapping
    public List<Courier> getAllCouriers() {
        return courierRepo.findAll();
    }

    @PutMapping("/{id}")
    public Courier updateCourier(@PathVariable Long id,
                                 @Valid @RequestBody AdminCreateCourierRequest request) {
        Courier courier = courierRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        UserEntity user = userRepo.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        courier.setUserEntity(user);
        return courierRepo.save(courier);
    }

    @DeleteMapping("/{id}")
    public void deleteCourier(@PathVariable Long id) {
        Courier courier = courierRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));
        courierRepo.delete(courier);
    }
}

