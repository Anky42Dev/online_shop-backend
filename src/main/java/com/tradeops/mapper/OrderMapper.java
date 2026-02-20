package com.tradeops.mapper;

import com.tradeops.models.dto.OrderItemDto;
import com.shop.onlineshop.models.entity.*;
import com.tradeops.models.entity.OrderEntity;
import com.tradeops.models.entity.OrderItemEntity;
import com.tradeops.models.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "deliveryCity", source = "city")
    @Mapping(target = "deliveryAddress", source = "address")
    @Mapping(target = "totalAmount", source = "totalPrice")
    @Mapping(target = "items", source = "orderItems")
    OrderResponse toResponse(OrderEntity entity);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "priceAtPurchase", source = "currentPrice")
    OrderItemDto toItemDto(OrderItemEntity entity);
}