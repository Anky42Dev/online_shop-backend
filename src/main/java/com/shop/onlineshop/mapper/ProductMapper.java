package com.shop.onlineshop.mapper;

import com.shop.onlineshop.models.entity.ProductEntity;
import com.shop.onlineshop.models.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "traderName", source = "trader.fullName")
    ProductResponse toResponse(ProductEntity entity);
}
