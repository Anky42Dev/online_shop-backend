package com.tradeops.controller;

import com.shop.onlineshop.models.request.*;
import com.tradeops.models.request.ChangePasswordRequest;
import com.tradeops.models.request.LoginRequest;
import com.tradeops.models.request.RefreshTokenRequest;
import com.tradeops.models.request.RegisterRequest;
import com.tradeops.models.response.LoginResponse;
import com.tradeops.models.response.RegistrationResponse;
import com.tradeops.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/register-trader")
    public ResponseEntity<RegistrationResponse> registerTrader(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.status(201).body(userService.registerTrader(registerRequest));
    }

    @PostMapping("/register-customer")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity<RegistrationResponse> registerCustomer(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.status(201).body(userService.registerCustomer(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.login(request,response));
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

