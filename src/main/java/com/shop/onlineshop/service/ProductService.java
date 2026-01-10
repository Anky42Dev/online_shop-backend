package com.shop.onlineshop.service;

import com.shop.onlineshop.models.response.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
}