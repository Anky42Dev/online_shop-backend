package com.tradeops.models.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}