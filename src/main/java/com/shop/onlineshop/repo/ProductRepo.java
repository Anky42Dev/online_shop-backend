package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByTraderIsNotNull();

    List<ProductEntity> findAllByTraderIsNull();
}
