package com.shop.onlineshop.mapper;

import com.shop.onlineshop.models.dto.CartItemDto;
import com.shop.onlineshop.models.entity.*;
import com.shop.onlineshop.models.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalPrice", expression = "java(calculateTotal(entity))")
    CartResponse toResponse(CartEntity entity);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "pricePerUnit", source = "product.price")
    @Mapping(target = "totalPrice", expression = "java(item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
    CartItemDto toItemDto(CartItemEntity item);

    default BigDecimal calculateTotal(CartEntity cart) {
        if (cart.getCartItems() == null) return BigDecimal.ZERO;
        return cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}