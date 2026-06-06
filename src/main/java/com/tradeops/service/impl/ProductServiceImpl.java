package com.tradeops.service.impl;

import com.tradeops.mapper.ProductMapper;
import com.tradeops.models.response.PageResponse;
import com.tradeops.models.response.ProductResponse;
import com.tradeops.repo.ProductRepo;
import com.tradeops.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductMapper productMapper;

    @Override
    public PageResponse<ProductResponse> getAllProducts(Long categoryId, Long traderId, String search, Pageable pageable) {
        String normalizedSearch = (search != null && search.isBlank()) ? null : search;

        return PageResponse.from(
                productRepo.findAllWithFilters(categoryId, traderId, normalizedSearch, pageable)
                        .map(productMapper::toResponse)
        );
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepo.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}