package com.shop.onlineshop.service.impl;

import com.shop.onlineshop.mapper.OrderMapper;
import com.shop.onlineshop.models.request.PlaceOrderRequest;
import com.shop.onlineshop.models.response.OrderResponse;
import com.shop.onlineshop.models.entity.*;
import com.shop.onlineshop.models.model.OrderStatus;
import com.shop.onlineshop.repo.*;
import com.shop.onlineshop.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final CustomerRepo customerRepo;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse placeOrder(UserEntity user, PlaceOrderRequest request) {
        CustomerEntity customer = customerRepo.findByUserEntity_Username(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        CartEntity cart = cartRepo.findByCustomer_Id(customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with empty cart");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerEntity(customer);
        order.setCity(request.city());
        order.setAddress(request.address());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        UserEntity trader = cart.getCartItems().get(0).getProduct().getTrader();
        order.setTrader(trader);

        List<OrderItemEntity> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemEntity cartItem : cart.getCartItems()) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            // Snapshot current price
            orderItem.setCurrentPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);

            BigDecimal itemTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalAmount);

        OrderEntity savedOrder = orderRepo.save(order);

        cart.getCartItems().clear();
        cartRepo.save(cart);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(UserEntity user) {
        return orderRepo.findAllByCustomerEntity_UserEntity_Username(user.getUsername())
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}