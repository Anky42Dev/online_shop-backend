package com.shop.onlineshop.service;

import com.shop.onlineshop.models.request.PlaceOrderRequest;
import com.shop.onlineshop.models.response.OrderResponse;
import com.shop.onlineshop.models.entity.UserEntity;
import java.util.List;

public interface CustomerOrderService {
    OrderResponse placeOrder(UserEntity user, PlaceOrderRequest request);
    List<OrderResponse> getMyOrders(UserEntity user);
}