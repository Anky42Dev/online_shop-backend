package com.tradeops.controller;

import com.tradeops.models.response.ProductResponse;
import com.tradeops.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // BE-011: добавлен @RequestParam search — регистронезависимый поиск по name/description
    // Пример: GET /api/v1/products?search=ноутбук&categoryId=3
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long traderId,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(productService.getAllProducts(categoryId, traderId, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}