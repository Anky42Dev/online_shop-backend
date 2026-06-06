package com.tradeops.service;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.response.TraderOrderResponse;

import java.util.List;

public interface TraderOrderService {

    List<TraderOrderResponse> getMyOrders(UserEntity trader, OrderStatus statusFilter);

    TraderOrderResponse getMyOrderById(UserEntity trader, Long orderId);
}