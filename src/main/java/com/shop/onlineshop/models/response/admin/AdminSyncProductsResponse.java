package com.shop.onlineshop.models.response.admin;

import java.util.List;

public record AdminSyncProductsResponse(
        List<AdminSyncProductDto> products
) {}

