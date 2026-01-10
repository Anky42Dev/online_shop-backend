package com.shop.onlineshop.service;

import com.shop.onlineshop.models.request.AddToCartRequest;
import com.shop.onlineshop.models.response.CartResponse;
import com.shop.onlineshop.models.entity.UserEntity;

public interface CustomerCartService {
    CartResponse getMyCart(UserEntity user);
    CartResponse addToCart(UserEntity user, AddToCartRequest request);
    void clearCart(UserEntity user);
}