package com.tradeops.service;

import com.tradeops.models.response.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts(Long categoryId, Long traderId);
    ProductResponse getProductById(Long id);
}