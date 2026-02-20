package com.tradeops.service;

import com.tradeops.models.response.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
}