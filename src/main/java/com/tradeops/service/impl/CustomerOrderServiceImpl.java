package com.tradeops.service.impl;

import com.tradeops.mapper.OrderMapper;
import com.tradeops.models.entity.*;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.request.PlaceOrderRequest;
import com.tradeops.models.response.OrderResponse;
import com.tradeops.repo.*;
import com.tradeops.service.CustomerOrderService;
import com.tradeops.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final CustomerRepo customerRepo;
    private final DeliveryTaskRepo deliveryTaskRepo;
    private final CourierRepo courierRepo;
    private final OrderMapper orderMapper;
    private final EmailService emailService;          // BE-010

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
        order.setStatus(OrderStatus.ASSIGNED);

        UserEntity trader = cart.getCartItems().get(0).getProduct().getTrader();
        order.setTrader(trader);

        List<OrderItemEntity> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemEntity cartItem : cart.getCartItems()) {
            if (cartItem.getProduct().getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + cartItem.getProduct().getName());
            }
            cartItem.getProduct().setStockQuantity(
                    cartItem.getProduct().getStockQuantity() - cartItem.getQuantity());

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setCurrentPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);

            BigDecimal itemTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalAmount);

        OrderEntity savedOrder = orderRepo.save(order);

        // ── BE-010: уведомление трейдеру о новом заказе ───────────────────────
        // try-catch изолирует ошибку отправки от основной транзакции
        try {
            emailService.sendNewOrderNotification(
                    trader.getEmail(), savedOrder.getId(), totalAmount);
        } catch (Exception e) {
            log.error("Failed to send new order notification to trader {}: {}",
                    trader.getEmail(), e.getMessage());
        }
        // ─────────────────────────────────────────────────────────────────────

        List<Courier> couriers = courierRepo.findAll();

        if (!couriers.isEmpty()) {
            Courier randomCourier = couriers.get(0);

            DeliveryTaskEntity task = new DeliveryTaskEntity();
            task.setOrderEntity(savedOrder);
            task.setCourier(randomCourier);
            task.setOrderStatus(OrderStatus.ASSIGNED);

            deliveryTaskRepo.save(task);

            savedOrder.setStatus(OrderStatus.ASSIGNED);
            orderRepo.save(savedOrder);

            log.info("System automatically assigned Order {} to Courier {}",
                    savedOrder.getId(), randomCourier.getUserEntity().getUsername());
        } else {
            log.warn("No couriers found! Order {} remains unassigned.", savedOrder.getId());
        }

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