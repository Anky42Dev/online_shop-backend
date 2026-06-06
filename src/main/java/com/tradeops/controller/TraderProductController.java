package com.tradeops.controller;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.TraderProductRequest;
import com.tradeops.models.request.UpdateStockRequest;
import com.tradeops.models.response.TraderProductResponse;
import com.tradeops.service.TraderProductService;
import com.tradeops.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trader/products")
@RequiredArgsConstructor
public class TraderProductController {

    private final TraderProductService traderProductService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<TraderProductResponse>> getMyProducts(
            @RequestParam(required = false) Long categoryId) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.getMyProducts(trader, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TraderProductResponse> getMyProductById(@PathVariable Long id) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.getMyProductById(trader, id));
    }

    @PostMapping
    public ResponseEntity<TraderProductResponse> createProduct(
            @Valid @RequestBody TraderProductRequest request) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traderProductService.createProduct(trader, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TraderProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody TraderProductRequest request) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.updateProduct(trader, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        UserEntity trader = userService.getCurrentUser();
        traderProductService.deleteProduct(trader, id);
        return ResponseEntity.noContent().build();
    }

    // ── BE-007 ────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/stock")
    public ResponseEntity<TraderProductResponse> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.updateStock(trader, id, request.quantity()));
    }
}