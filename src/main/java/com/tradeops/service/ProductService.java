package com.tradeops.service;

import com.tradeops.models.response.PageResponse;
import com.tradeops.models.response.ProductResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(Long categoryId, Long traderId, String search, Pageable pageable);
    ProductResponse getProductById(Long id);
}