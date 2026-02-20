package com.tradeops.mapper;

import com.tradeops.models.entity.ProductEntity;
import com.tradeops.models.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "traderName", source = "trader.fullName")
    ProductResponse toResponse(ProductEntity entity);
}
