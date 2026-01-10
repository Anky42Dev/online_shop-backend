package com.shop.onlineshop.models.request;

import jakarta.validation.constraints.NotBlank;

public record PlaceOrderRequest (
        @NotBlank
        String city,
        @NotBlank
        String address

) {
}
