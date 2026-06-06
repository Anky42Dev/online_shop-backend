package com.tradeops.controller;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.AddToCartRequest;
import com.tradeops.models.response.CartResponse;
import com.tradeops.service.CustomerCartService;
import com.tradeops.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CustomerCartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(cartService.getMyCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(cartService.addToCart(user, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        UserEntity user = userService.getCurrentUser();
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }

    // ── BE-009 ────────────────────────────────────────────────────────────────

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long productId) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(cartService.removeFromCart(user, productId));
    }
}