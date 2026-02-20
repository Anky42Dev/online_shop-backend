package com.tradeops.service.impl;

import com.tradeops.mapper.ProductMapper;
import com.tradeops.models.response.ProductResponse;
import com.tradeops.repo.ProductRepo;
import com.tradeops.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getAllProducts(Long categoryId, Long traderId) {
        List<ProductEntity> products;

        if (categoryId != null && traderId != null) {
            products = productRepo.findAllByCategoryIdAndTraderId(categoryId, traderId);
        } else if (categoryId != null) {
            products = productRepo.findAllByCategoryId(categoryId);
        } else if (traderId != null) {
            products = productRepo.findAllByTraderId(traderId);
        } else {
            products = productRepo.findAll();
        }

        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepo.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}