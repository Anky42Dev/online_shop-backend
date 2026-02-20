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
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll().stream()
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