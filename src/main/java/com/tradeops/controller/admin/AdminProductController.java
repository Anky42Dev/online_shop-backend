package com.tradeops.controller.admin;

import com.tradeops.models.entity.CategoryEntity;
import com.tradeops.models.entity.ProductEntity;
import com.tradeops.models.request.AdminProductRequest;
import com.tradeops.models.response.PageResponse;
import com.tradeops.repo.CategoryRepo;
import com.tradeops.repo.ProductRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminProductController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    @GetMapping
    public ResponseEntity<PageResponse<ProductEntity>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (size > MAX_PAGE_SIZE) {
            return ResponseEntity.badRequest().build();
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return ResponseEntity.ok(PageResponse.from(productRepo.findAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
                productRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"))
        );
    }

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@Valid @RequestBody AdminProductRequest req) {
        CategoryEntity category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        ProductEntity product = new ProductEntity();
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setStockQuantity(req.stockQuantity());
        product.setCategory(category);
        product.setTrader(null);

        return ResponseEntity.ok(productRepo.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(
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

        return ResponseEntity.ok(productRepo.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        ProductEntity product = productRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepo.delete(product);
        return ResponseEntity.noContent().build();
    }
}