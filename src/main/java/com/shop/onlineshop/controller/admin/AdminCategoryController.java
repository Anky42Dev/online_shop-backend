package com.shop.onlineshop.controller.admin;

import com.shop.onlineshop.models.entity.CategoryEntity;
import com.shop.onlineshop.models.request.AdminCategoryRequest;
import com.shop.onlineshop.repo.CategoryRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCategoryController {

    private final CategoryRepo categoryRepo;

    @GetMapping
    public List<CategoryEntity> getAllCategories() {
        return categoryRepo.findAll();
    }

    @GetMapping("/{id}")
    public CategoryEntity getCategory(@PathVariable Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @PostMapping
    public CategoryEntity createCategory(
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        if (categoryRepo.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        CategoryEntity category = new CategoryEntity();
        category.setName(request.name());

        return categoryRepo.save(category);
    }

    @PutMapping("/{id}")
    public CategoryEntity updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        CategoryEntity category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(request.name());
        return categoryRepo.save(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        CategoryEntity category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with products");
        }

        categoryRepo.delete(category);
    }
}

