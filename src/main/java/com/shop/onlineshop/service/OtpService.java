package com.shop.onlineshop.service;

import com.shop.onlineshop.models.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class OtpService {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public OtpService(PasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void generateAndAssign(UserEntity user) {
        String otp = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        user.setOtpCode(passwordEncoder.encode(otp));
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setOtpAttempts(0);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
            log.info("OTP email sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", user.getEmail(), e.getMessage());
        }

        log.info("OTP generated for user {}", user.getUsername());
    }

    public boolean verify(UserEntity user, String rawOtp) {
        if (user.getOtpCode() == null || user.getOtpExpiresAt() == null) {
            return false;
        }
        if (user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (user.getOtpAttempts() >= 5) {
            log.warn("User {} exceeded OTP attempts", user.getUsername());
            return false;
        }

        if (!passwordEncoder.matches(rawOtp, user.getOtpCode())) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            return false;
        }

        user.setOtpAttempts(0);
        return true;
    }

    public void clear(UserEntity user) {
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        user.setOtpAttempts(0);
    }
}
