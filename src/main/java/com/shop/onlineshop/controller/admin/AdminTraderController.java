package com.shop.onlineshop.controller.admin;

import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.models.response.admin.AdminTraderResponse;
import com.shop.onlineshop.repo.UserEntityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/traders")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminTraderController {

    private final UserEntityRepo userRepo;

    @GetMapping("/pending")
    public List<AdminTraderResponse> getPendingTraders() {
        return userRepo.findAll().stream()
                .filter(user ->
                        user.getRoles().stream()
                                .anyMatch(r -> r.getName().equals("ROLE_TRADER"))
                                && !user.isApproved()
                                && !user.isRejected()
                )
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/{id}/approve")
    public void approveTrader(@PathVariable Long id) {
        UserEntity trader = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trader not found"));

        trader.setApproved(true);
        trader.setRejected(false);
        userRepo.save(trader);
    }

    @PostMapping("/{id}/reject")
    public void rejectTrader(@PathVariable Long id) {
        UserEntity trader = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trader not found"));

        trader.setRejected(true);
        trader.setApproved(false);
        userRepo.save(trader);
    }

    private AdminTraderResponse toResponse(UserEntity user) {
        return new AdminTraderResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }
}

