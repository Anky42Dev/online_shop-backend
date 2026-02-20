package com.tradeops.mapper;

import com.tradeops.models.dto.DeliveryTaskDTO;
import com.tradeops.models.dto.OrderItemDto;
import com.tradeops.models.entity.DeliveryTaskEntity;
import com.tradeops.models.entity.OrderItemEntity;
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
