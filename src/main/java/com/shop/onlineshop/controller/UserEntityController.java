package com.shop.onlineshop.controller;

import com.shop.onlineshop.models.request.*;
import com.shop.onlineshop.models.response.JWTResponse;
import com.shop.onlineshop.models.response.LoginResponse;
import com.shop.onlineshop.models.response.RegistrationResponse;
import com.shop.onlineshop.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/auth"})
public class UserEntityController {

    private final UserServiceImpl userService;

    public UserEntityController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRADER')")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.status(201).body(userService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.login(request,response));
    }

    @PostMapping("/login/otp")
    public ResponseEntity<LoginResponse> verifyOtp(
            @RequestBody OtpVerifyRequest request
    ) {
        return ResponseEntity.ok(userService.verifyOtp(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(userService.refreshToken(refreshTokenRequest));
    }

}

