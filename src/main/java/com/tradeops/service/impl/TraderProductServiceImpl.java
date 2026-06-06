package com.tradeops.service.impl;

import com.tradeops.exceptions.ResourceNotFoundException;
import com.tradeops.models.entity.CategoryEntity;
import com.tradeops.models.entity.ProductEntity;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.request.TraderProductRequest;
import com.tradeops.models.response.TraderProductResponse;
import com.tradeops.repo.CategoryRepo;
import com.tradeops.repo.OrderRepo;
import com.tradeops.repo.ProductRepo;
import com.tradeops.service.TraderProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraderProductServiceImpl implements TraderProductService {

    private static final List<OrderStatus> TERMINAL_STATUSES = List.of(
            OrderStatus.DELIVERED, OrderStatus.FAILED);

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final OrderRepo orderRepo;

    @Override
    @Transactional(readOnly = true)
    public List<TraderProductResponse> getMyProducts(UserEntity trader, Long categoryId) {
        List<ProductEntity> products = (categoryId != null)
                ? productRepo.findAllByTraderIdAndCategoryId(trader.getId(), categoryId)
                : productRepo.findAllByTraderId(trader.getId());

        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TraderProductResponse getMyProductById(UserEntity trader, Long productId) {
        return toResponse(findOwnedProduct(trader.getId(), productId));
    }

    @Override
    @Transactional
    public TraderProductResponse createProduct(UserEntity trader, TraderProductRequest request) {
        CategoryEntity category = findCategory(request.categoryId());

        ProductEntity product = new ProductEntity();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
        product.setTrader(trader);

        ProductEntity saved = productRepo.save(product);
        log.info("Trader {} created product {}", trader.getId(), saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TraderProductResponse updateProduct(UserEntity trader, Long productId, TraderProductRequest request) {
        ProductEntity product = findOwnedProduct(trader.getId(), productId);
        CategoryEntity category = findCategory(request.categoryId());

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);

        ProductEntity saved = productRepo.save(product);
        log.info("Trader {} updated product {}", trader.getId(), saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(UserEntity trader, Long productId) {
        ProductEntity product = findOwnedProduct(trader.getId(), productId);

        if (orderRepo.existsActiveOrdersByProductId(productId, TERMINAL_STATUSES)) {
            throw new IllegalStateException("Cannot delete product with active orders");
        }

        productRepo.delete(product);
        log.info("Trader {} deleted product {}", trader.getId(), productId);
    }

    // --- helpers ---

    private ProductEntity findOwnedProduct(Long traderId, Long productId) {
        return productRepo.findByIdAndTrader_Id(productId, traderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product " + productId + " not found or does not belong to trader " + traderId));
    }

    private CategoryEntity findCategory(Long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category " + categoryId + " not found"));
    }

    private TraderProductResponse toResponse(ProductEntity p) {
        return new TraderProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getCategory() != null ? p.getCategory().getId() : null
        );
    }
}