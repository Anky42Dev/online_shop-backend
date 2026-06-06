package com.tradeops.controller;

import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.request.TraderProductRequest;
import com.tradeops.models.request.UpdateStockRequest;
import com.tradeops.models.response.TraderProductResponse;
import com.tradeops.service.TraderProductService;
import com.tradeops.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name        = "Trader API",
        description = "Управление товарами, заказами и аналитикой для роли Трейдера"
)
@SecurityRequirement(name = "bearerAuth")
public class TraderProductController {

    private final TraderProductService traderProductService;
    private final UserService userService;

    @GetMapping
    @Operation(
            summary     = "Получить список своих товаров",
            description = "Возвращает все товары текущего трейдера. "
                    + "Можно фильтровать по categoryId."
    )
    public ResponseEntity<List<TraderProductResponse>> getMyProducts(
            @Parameter(description = "ID категории для фильтрации (опционально)")
            @RequestParam(required = false) Long categoryId) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.getMyProducts(trader, categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID")
    public ResponseEntity<TraderProductResponse> getMyProductById(
            @Parameter(description = "ID товара") @PathVariable Long id) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.getMyProductById(trader, id));
    }

    @PostMapping
    @Operation(
            summary     = "Создать новый товар",
            description = "Создаёт товар и привязывает его к текущему трейдеру."
    )
    public ResponseEntity<TraderProductResponse> createProduct(
            @Valid @RequestBody TraderProductRequest request) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traderProductService.createProduct(trader, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные товара")
    public ResponseEntity<TraderProductResponse> updateProduct(
            @Parameter(description = "ID товара") @PathVariable Long id,
            @Valid @RequestBody TraderProductRequest request) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.updateProduct(trader, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary     = "Удалить товар",
            description = "Удаление запрещено, если по товару есть активные заказы."
    )
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID товара") @PathVariable Long id) {
        UserEntity trader = userService.getCurrentUser();
        traderProductService.deleteProduct(trader, id);
        return ResponseEntity.noContent().build();
    }

    // ── BE-007 ────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/stock")
    @Operation(
            summary     = "Обновить остаток товара",
            description = "Частичное обновление: изменяет только stockQuantity без затрагивания других полей."
    )
    public ResponseEntity<TraderProductResponse> updateStock(
            @Parameter(description = "ID товара") @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request) {
        UserEntity trader = userService.getCurrentUser();
        return ResponseEntity.ok(traderProductService.updateStock(trader, id, request.quantity()));
    }
}