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

    // BE-011: if-else каскад заменён единственным вызовом findAllWithFilters.
    // JPQL-запрос сам обрабатывает все комбинации null/non-null через ":x IS NULL OR ...".
    @Override
    public List<ProductResponse> getAllProducts(Long categoryId, Long traderId, String search) {
        // Пустая строка эквивалентна отсутствию фильтра — нормализуем на входе
        String normalizedSearch = (search != null && search.isBlank()) ? null : search;

        return productRepo.findAllWithFilters(categoryId, traderId, normalizedSearch)
                .stream()
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