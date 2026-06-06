package com.tradeops.service;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.TraderProductRequest;
import com.tradeops.models.response.TraderProductResponse;

import java.util.List;

public interface TraderProductService {

    List<TraderProductResponse> getMyProducts(UserEntity trader, Long categoryId);

    TraderProductResponse getMyProductById(UserEntity trader, Long productId);

    TraderProductResponse createProduct(UserEntity trader, TraderProductRequest request);

    TraderProductResponse updateProduct(UserEntity trader, Long productId, TraderProductRequest request);

    void deleteProduct(UserEntity trader, Long productId);
}