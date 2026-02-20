package com.tradeops.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest (
        @NotNull
        Long productId,
        @NotNull
        Long traderId,
        @Min(1)
        int quantity
){

}

