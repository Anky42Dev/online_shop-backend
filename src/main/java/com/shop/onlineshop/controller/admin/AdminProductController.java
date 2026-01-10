package com.shop.onlineshop.controller.admin;

import com.shop.onlineshop.models.entity.CategoryEntity;
import com.shop.onlineshop.models.entity.ProductEntity;
import com.shop.onlineshop.models.request.AdminProductRequest;
import com.shop.onlineshop.repo.CategoryRepo;
import com.shop.onlineshop.repo.ProductRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminProductController {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    @GetMapping
    public List<ProductEntity> getAllProducts() {
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    public ProductEntity getProduct(@PathVariable Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @PostMapping
    public ProductEntity createProduct(@Valid @RequestBody AdminProductRequest req) {

        CategoryEntity category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        ProductEntity product = new ProductEntity();
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setStockQuantity(req.stockQuantity());
        product.setCategory(category);
        product.setTrader(null);

        return productRepo.save(product);
    }

    @PutMapping("/{id}")
    public ProductEntity updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequest req
    ) {
        ProductEntity product = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CategoryEntity category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setStockQuantity(req.stockQuantity());
        product.setCategory(category);

        return productRepo.save(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        ProductEntity product = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        productRepo.delete(product);
    }
}

