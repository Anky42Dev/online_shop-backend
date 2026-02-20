package com.tradeops.service;

import com.tradeops.models.request.AddToCartRequest;
import com.tradeops.models.response.CartResponse;
import com.tradeops.models.entity.UserEntity;

public interface CustomerCartService {
    CartResponse getMyCart(UserEntity user);
    CartResponse addToCart(UserEntity user, AddToCartRequest request);
    void clearCart(UserEntity user);
}