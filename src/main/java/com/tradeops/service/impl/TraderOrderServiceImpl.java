package com.tradeops.service.impl;

import com.tradeops.exceptions.ResourceNotFoundException;
import com.tradeops.models.dto.OrderItemDto;
import com.tradeops.models.entity.OrderEntity;
import com.tradeops.models.entity.OrderItemEntity;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.response.TraderOrderResponse;
import com.tradeops.repo.OrderRepo;
import com.tradeops.service.TraderOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraderOrderServiceImpl implements TraderOrderService {

    private final OrderRepo orderRepo;

    @Override
    @Transactional(readOnly = true)
    public List<TraderOrderResponse> getMyOrders(UserEntity trader, OrderStatus statusFilter) {
        log.info("Trader {} fetching orders with statusFilter={}", trader.getId(), statusFilter);
        List<OrderEntity> orders = (statusFilter != null)
                ? orderRepo.findAllByTrader_IdAndStatus(trader.getId(), statusFilter)
                : orderRepo.findAllByTrader_Id(trader.getId());

        log.info("Trader {} fetched {} orders", trader.getId(), orders.size());
        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TraderOrderResponse getMyOrderById(UserEntity trader, Long orderId) {
        log.info("Trader {} requested order {}", trader.getId(), orderId);
        OrderEntity order = orderRepo.findByIdAndTrader_Id(orderId, trader.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order " + orderId + " not found"));
        return toResponse(order);
    }

    // --- helpers ---

    private TraderOrderResponse toResponse(OrderEntity order) {
        UserEntity customer = order.getCustomerEntity() != null
                ? order.getCustomerEntity().getUserEntity()
                : null;

        return new TraderOrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCity(),
                order.getAddress(),
                customer != null ? customer.getFullName() : null,
                customer != null ? customer.getEmail() : null,
                toItemDtos(order.getOrderItems())
        );
    }

    private List<OrderItemDto> toItemDtos(List<OrderItemEntity> items) {
        if (items == null) return List.of();
        return items.stream().map(item -> {
            OrderItemDto dto = new OrderItemDto();
            dto.setProductName(item.getProduct() != null ? item.getProduct().getName() : null);
            dto.setQuantity(item.getQuantity());
            dto.setPriceAtPurchase(item.getCurrentPrice());
            return dto;
        }).toList();
    }
}