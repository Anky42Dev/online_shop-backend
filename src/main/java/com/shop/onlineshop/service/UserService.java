package com.shop.onlineshop.service;

import com.shop.onlineshop.models.entity.Role;
import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.models.request.ChangePasswordRequest;
import com.shop.onlineshop.models.request.LoginRequest;
import com.shop.onlineshop.models.request.OtpVerifyRequest;
import com.shop.onlineshop.models.request.RegisterRequest;
import com.shop.onlineshop.models.response.JWTResponse;
import com.shop.onlineshop.models.response.LoginResponse;
import com.shop.onlineshop.models.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {
    RegistrationResponse register(RegisterRequest request);
    UserEntity register(RegisterRequest request, Role role);

    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);
    //might be changed
    JWTResponse verifyOtp(OtpVerifyRequest request);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void logout(HttpServletResponse response);
    UserEntity getCurrentUser();
}
