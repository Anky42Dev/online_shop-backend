package com.shop.onlineshop.service.impl;

import com.shop.onlineshop.dataservices.RoleDataService;
import com.shop.onlineshop.dataservices.UserEntityDataService;
import com.shop.onlineshop.exceptions.UserAlreadyExistsException;
import com.shop.onlineshop.models.entity.Role;
import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.models.request.*;
import com.shop.onlineshop.models.response.*;
import com.shop.onlineshop.security.service.JWTService;
import com.shop.onlineshop.service.CustomUserDetailsService;
import com.shop.onlineshop.service.OtpService;
import com.shop.onlineshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    @Transactional
    public RegistrationResponse registerTrader(RegisterRequest req) {
        validateRegistration(req);

        UserEntity user = createUserEntity(req);

        Role role = roleData.findByName("ROLE_TRADER");
        user.setRoles(List.of(role));

        user.setVerified(false);
        user.setApproved(false);

        otpService.generateAndAssign(user);
        userData.saveUserEntity(user);

        return new RegistrationResponse(
                "Trader registration successful! Please verify OTP and wait for Admin approval.",
                new RegistrationResponse.User(user.getId(), user.getFullName(), user.getEmail()),
                null, null, true, 300
        );
    }

    @Override
    @Transactional
    public RegistrationResponse registerCustomer(RegisterRequest req) {
        validateRegistration(req);

        UserEntity user = createUserEntity(req);

        Role role = roleData.findByName("ROLE_CUSTOMER");
        user.setRoles(List.of(role));

        user.setVerified(true);
        user.setApproved(true);
        user.setRejected(false);

        userData.saveUserEntity(user);

        return new RegistrationResponse(
                "Customer registered successfully.",
                new RegistrationResponse.User(user.getId(), user.getFullName(), user.getEmail()),
                null, null, false, null
        );
    }

    @Override
    @Transactional
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
        user.setVerified(true);

        userData.saveUserEntity(user);

        return user;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserEntity user = userData.getUserEntityByUsernameOrThrow(request.username());

        boolean isTrader = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_TRADER"));
        if (isTrader && !user.isApproved()) {
            throw new BadCredentialsException("Your account is pending approval by Admin.");
        }

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
                    jwt.accessToken(),
                    jwt.refreshToken(),
                    user.getRoles().getFirst().getName(),
                    user.getId(),
                    false,
                    null
            );
        }

        otpService.generateAndAssign(user);
        userData.updateUserEntity(user);

        return new LoginResponse(
                null,
                null,
                user.getRoles().getFirst().getName(),
                user.getId(),
                true,
                300
        );
    }

    @Override
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public LoginResponse verifyOtp(OtpVerifyRequest request) {
        UserEntity user = userData.getUserEntityByUsernameOrThrow(request.username());

        if (!otpService.verify(user, request.otp())) {
            userData.updateUserEntity(user);

            if (user.getOtpAttempts() >= 5) {
                throw new BadCredentialsException("Too many OTP attempts. Request a new OTP.");
            }

            throw new BadCredentialsException("Invalid or expired OTP");
        }

        otpService.clear(user);

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

        JWTResponse jwt = issueTokens(auth);
        return new LoginResponse(
                jwt.accessToken(),
                jwt.refreshToken(),
                user.getRoles().getFirst().getName(),
                user.getId(),
                false,
                null
        );
    }

    @Override
    @Transactional
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
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String incomingRefreshToken = request.refreshToken();

        String username;
        try {
            username = jwtService.extractUserName(incomingRefreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        UserEntity user = userData.getUserEntityByUsernameOrThrow(username);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        if (!jwtService.validateToken(incomingRefreshToken, userDetails)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

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
                jwt.accessToken(),
                jwt.refreshToken(),
                user.getRoles().getFirst().getName(),
                user.getId(),
                false,
                null
        );
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

    private UserEntity createUserEntity(RegisterRequest req) {
        UserEntity user = new UserEntity();
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setUsername(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private void validateRegistration(RegisterRequest req) {
        if (!req.password().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userData.existsByEmail(req.email())) {
            throw new UserAlreadyExistsException("Email already registered");
        }
    }


}
