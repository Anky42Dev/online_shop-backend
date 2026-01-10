package com.shop.onlineshop.controller;

import com.shop.onlineshop.models.request.ChangePasswordRequest;
import com.shop.onlineshop.models.request.LoginRequest;
import com.shop.onlineshop.models.request.OtpVerifyRequest;
import com.shop.onlineshop.models.request.RegisterRequest;
import com.shop.onlineshop.models.response.JWTResponse;
import com.shop.onlineshop.models.response.LoginResponse;
import com.shop.onlineshop.models.response.RegistrationResponse;
import com.shop.onlineshop.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.login(request,response));
    }

    @PostMapping("/login/otp")
    public ResponseEntity<JWTResponse> verifyOtp(
            @RequestBody OtpVerifyRequest request
    ) {
        return ResponseEntity.ok(userService.verifyOtp(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok("Logout successful");
    }
}

