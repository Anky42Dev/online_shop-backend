package com.shop.onlineshop.mapper;

import com.shop.onlineshop.models.dto.DeliveryTaskDTO;
import com.shop.onlineshop.models.dto.OrderItemDto;
import com.shop.onlineshop.models.entity.DeliveryTaskEntity;
import com.shop.onlineshop.models.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryTaskMapper {
    @Mapping(target = "city", source = "orderEntity.city")
    @Mapping(target = "address", source = "orderEntity.address")
    @Mapping(target = "orderItems", source = "orderEntity.orderItems")
    @Mapping(target = "price", source = "orderEntity.totalPrice")
    DeliveryTaskDTO toDTO(DeliveryTaskEntity deliveryTaskEntity);

    OrderItemDto toItemDto(OrderItemEntity itemEntity);

    List<DeliveryTaskDTO> toDTO(List<DeliveryTaskEntity> deliveryTaskEntities);
}
