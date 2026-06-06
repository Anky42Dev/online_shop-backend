package com.tradeops.controller.admin;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.response.admin.AdminTraderResponse;
import com.tradeops.repo.UserEntityRepo;
import com.tradeops.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/traders")
@RequiredArgsConstructor
@Slf4j                                               // BE-010: нужен для log.error в catch-блоках
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminTraderController {

    private final UserEntityRepo userRepo;
    private final EmailService emailService;          // BE-010

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

        // ── BE-010 ────────────────────────────────────────────────────────────
        try {
            emailService.sendTraderApprovalNotification(trader.getEmail(), true);
        } catch (Exception e) {
            log.error("Failed to send approval notification to trader {}: {}",
                    trader.getEmail(), e.getMessage());
        }
        // ─────────────────────────────────────────────────────────────────────
    }

    @PostMapping("/{id}/reject")
    public void rejectTrader(@PathVariable Long id) {
        UserEntity trader = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trader not found"));

        trader.setRejected(true);
        trader.setApproved(false);
        userRepo.save(trader);

        // ── BE-010 ────────────────────────────────────────────────────────────
        try {
            emailService.sendTraderApprovalNotification(trader.getEmail(), false);
        } catch (Exception e) {
            log.error("Failed to send rejection notification to trader {}: {}",
                    trader.getEmail(), e.getMessage());
        }
        // ─────────────────────────────────────────────────────────────────────
    }

    private AdminTraderResponse toResponse(UserEntity user) {
        return new AdminTraderResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }
}