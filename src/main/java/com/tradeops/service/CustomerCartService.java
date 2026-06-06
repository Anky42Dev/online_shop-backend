package com.tradeops.service;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.AddToCartRequest;
import com.tradeops.models.response.CartResponse;

public interface CustomerCartService {
    CartResponse getMyCart(UserEntity user);
    CartResponse addToCart(UserEntity user, AddToCartRequest request);
    void clearCart(UserEntity user);

    // BE-009 — удаление одной позиции из корзины
    CartResponse removeFromCart(UserEntity user, Long productId);
}