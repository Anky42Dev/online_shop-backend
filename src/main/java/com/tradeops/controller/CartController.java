package com.tradeops.controller;

import com.tradeops.models.request.AddToCartRequest;
import com.tradeops.models.response.CartResponse;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.service.CustomerCartService;
import com.tradeops.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CustomerCartService cartService;
    private final UserService userService; // Твой существующий сервис для получения юзера

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(cartService.getMyCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(cartService.addToCart(user, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        UserEntity user = userService.getCurrentUser();
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
