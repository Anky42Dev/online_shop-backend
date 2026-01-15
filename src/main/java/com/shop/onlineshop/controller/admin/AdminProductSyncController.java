package com.shop.onlineshop.controller.admin;

import com.shop.onlineshop.models.entity.OrderEntity;
import com.shop.onlineshop.models.entity.OrderItemEntity;
import com.shop.onlineshop.models.entity.ProductEntity;
import com.shop.onlineshop.models.response.admin.*;
import com.shop.onlineshop.repo.OrderRepo;
import com.shop.onlineshop.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TRADER')")
public class AdminProductSyncController {

    private static final int PAGE_SIZE = 50;

    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    @GetMapping("/products")
    public AdminSyncProductsResponse syncProducts(
            @RequestParam(required = false) String since,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<ProductEntity> productsPage =
                productRepo.findAll(PageRequest.of(page, PAGE_SIZE));

        List<AdminSyncProductDto> products =
                productsPage.getContent()
                        .stream()
                        .map(this::mapToDto)
                        .toList();

        return new AdminSyncProductsResponse(products);
    }

    private AdminSyncProductDto mapToDto(ProductEntity product) {
        return new AdminSyncProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getName(),
                buildVersion(product)
        );
    }

    private String buildVersion(ProductEntity product) {
        try {
            String payload = String.join("|",
                    String.valueOf(product.getId()),
                    product.getName(),
                    product.getPrice().toPlainString(),
                    String.valueOf(product.getStockQuantity()),
                    product.getCategory().getName()
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return String.valueOf(product.getId());
        }
    }

    @GetMapping("/orders")
    public AdminSyncOrdersResponse syncOrders(
            @RequestParam(required = false) String since,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<OrderEntity> ordersPage =
                orderRepo.findAll(PageRequest.of(page, PAGE_SIZE));

        List<AdminSyncOrderDto> orders =
                ordersPage.getContent()
                        .stream()
                        .map(this::mapOrderToDto)
                        .toList();

        return new AdminSyncOrdersResponse(orders);
    }

    private AdminSyncOrderDto mapOrderToDto(OrderEntity order) {
        List<AdminSyncOrderItemDto> items = order.getOrderItems().stream()
                .map(this::mapOrderItemToDto)
                .toList();

        String customerEmail = order.getCustomerEntity() != null
                && order.getCustomerEntity().getUserEntity() != null
                ? order.getCustomerEntity().getUserEntity().getEmail()
                : null;

        return new AdminSyncOrderDto(
                order.getId(),
                customerEmail,
                items,
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getCity(),
                order.getAddress(),
                buildOrderVersion(order)
        );
    }

    private AdminSyncOrderItemDto mapOrderItemToDto(OrderItemEntity item) {
        return new AdminSyncOrderItemDto(
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProduct() != null ? item.getProduct().getName() : null,
                item.getQuantity(),
                item.getCurrentPrice()
        );
    }

    private String buildOrderVersion(OrderEntity order) {
        try {
            String payload = String.join("|",
                    String.valueOf(order.getId()),
                    order.getTotalPrice().toPlainString(),
                    order.getStatus().name(),
                    order.getCreatedAt().toString(),
                    order.getCity(),
                    order.getAddress(),
                    String.valueOf(order.getOrderItems().size())
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return String.valueOf(order.getId());
        }
    }
}

