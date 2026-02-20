package com.tradeops.service;

import com.tradeops.models.entity.Role;
import com.tradeops.models.entity.UserEntity;
import com.shop.onlineshop.models.request.*;
import com.tradeops.models.request.ChangePasswordRequest;
import com.tradeops.models.request.LoginRequest;
import com.tradeops.models.request.RefreshTokenRequest;
import com.tradeops.models.request.RegisterRequest;
import com.tradeops.models.response.LoginResponse;
import com.tradeops.models.response.RegistrationResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    RegistrationResponse registerTrader(RegisterRequest request);
    RegistrationResponse registerCustomer(RegisterRequest request);
    UserEntity register(RegisterRequest request, Role role);
    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    UserEntity getCurrentUser();
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
