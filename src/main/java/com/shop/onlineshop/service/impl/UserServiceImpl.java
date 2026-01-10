package com.shop.onlineshop.service.impl;

import com.shop.onlineshop.dataservices.RoleDataService;
import com.shop.onlineshop.dataservices.UserEntityDataService;
import com.shop.onlineshop.exceptions.UserAlreadyExistsException;
import com.shop.onlineshop.models.entity.Role;
import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.models.request.ChangePasswordRequest;
import com.shop.onlineshop.models.request.LoginRequest;
import com.shop.onlineshop.models.request.OtpVerifyRequest;
import com.shop.onlineshop.models.request.RegisterRequest;
import com.shop.onlineshop.models.response.JWTResponse;
import com.shop.onlineshop.models.response.LoginResponse;
import com.shop.onlineshop.models.response.RegisterResponse;
import com.shop.onlineshop.models.response.RegistrationResponse;
import com.shop.onlineshop.security.service.JWTService;
import com.shop.onlineshop.service.OtpService;
import com.shop.onlineshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserEntityDataService userData;

    private final RoleDataService roleData;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final OtpService otpService;

    @Value("${onlineshop.app.isproduction}")
    private boolean isProduction;


    @Override
    public RegistrationResponse register(RegisterRequest req) {
        if (!req.password().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userData.existsByEmail(req.email())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setUsername(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setCreatedAt(LocalDateTime.now());

        Role role = roleData.findByName("ROLE_USER");
        user.setRoles(List.of(role));

        userData.saveUserEntity(user);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName()))
                                .toList()
                );

        JWTResponse jwt = issueTokens(auth);

        return new RegistrationResponse(
                "Registration successful",
                new RegistrationResponse.User(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail()
                ),
                jwt.accessToken(),
                jwt.refreshToken()
        );
    }

    @Override
    public UserEntity register(RegisterRequest req, Role role) {
        if (!req.password().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userData.existsByEmail(req.email())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setUsername(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setCreatedAt(LocalDateTime.now());

        user.setRoles(List.of(role));

        userData.saveUserEntity(user);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName()))
                                .toList()
                );

        JWTResponse jwt = issueTokens(auth);

        return user;
    }


    @Override
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserEntity user = userData.getUserEntityByUsernameOrThrow(request.username());

        // If user has already successfully verified OTP before, do not send OTP again, just issue JWT tokens
        if (user.isVerified()) {
            Authentication auth =
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            user.getRoles().stream()
                                    .map(r -> new SimpleGrantedAuthority(r.getName()))
                                    .toList()
                    );

            JWTResponse jwt = issueTokens(auth);

            return new LoginResponse(
                    "Login successful",
                    null,
                    jwt.accessToken(),
                    jwt.refreshToken()
            );
        }

        otpService.generateAndAssign(user);
        userData.updateUserEntity(user);

        return new LoginResponse(
                "OTP sent",
                300,
                null,
                null
        );
    }

    @Override
    public JWTResponse verifyOtp(OtpVerifyRequest request) {

        UserEntity user = userData.getUserEntityByUsernameOrThrow(request.username());

        if (!otpService.verify(user, request.otp())) {
            userData.updateUserEntity(user);

            if (user.getOtpAttempts() >= 5) {
                throw new BadCredentialsException("Too many OTP attempts. Request a new OTP.");
            }

            throw new BadCredentialsException("Invalid or expired OTP");
        }

        otpService.clear(user);
        userData.updateUserEntity(user);

        // Mark user as verified after successful OTP verification
        user.setVerified(true);
        userData.updateUserEntity(user);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getRoles().stream()
                                .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.getName()))
                                .toList()
                );

        return issueTokens(auth);
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {

        UserEntity user = getCurrentUser();

        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }
        if (req.newPassword().length() < 8) {
            throw new IllegalArgumentException("Password too short");
        }

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userData.updateUserEntity(user);
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction);
        cookie.setPath("/api/v1");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


    @Override
    public UserEntity getCurrentUser() {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return userData.getUserEntityByUsernameOrThrow(username);
    }

    private JWTResponse issueTokens(Authentication auth) {
        String access = jwtService.generateToken(auth);
        String refresh = jwtService.generateRefreshToken(auth);

        return new JWTResponse(access,refresh);
    }

}
