package com.shop.onlineshop.controller.admin;

import com.shop.onlineshop.models.entity.ProductEntity;
import com.shop.onlineshop.models.response.admin.AdminSyncProductDto;
import com.shop.onlineshop.models.response.admin.AdminSyncProductsResponse;
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
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminProductSyncController {

    private static final int PAGE_SIZE = 50;

    private final ProductRepo productRepo;

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
}

