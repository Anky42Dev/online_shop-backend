package com.shop.onlineshop.controller;

import com.shop.onlineshop.models.entity.CategoryEntity;
import com.shop.onlineshop.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepo categoryRepo;

    @GetMapping
    public ResponseEntity<List<CategoryEntity>> getAllCategories() {
        return ResponseEntity.ok(categoryRepo.findAll());
    }
}
