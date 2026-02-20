package com.tradeops.service;

import com.tradeops.models.request.PlaceOrderRequest;
import com.tradeops.models.response.OrderResponse;
import com.tradeops.models.entity.UserEntity;
import java.util.List;

public interface CustomerOrderService {
    OrderResponse placeOrder(UserEntity user, PlaceOrderRequest request);
    List<OrderResponse> getMyOrders(UserEntity user);
}