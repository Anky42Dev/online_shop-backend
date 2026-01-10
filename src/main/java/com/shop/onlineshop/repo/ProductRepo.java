package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
}
