package com.tradeops.repo;

import com.tradeops.models.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByTraderIsNotNull();

    List<ProductEntity> findAllByTraderIsNull();

    List<ProductEntity> findAllByCategoryId(Long categoryId);

    List<ProductEntity> findAllByTraderId(Long traderId);

    List<ProductEntity> findAllByCategoryIdAndTraderId(Long categoryId, Long traderId);

    Optional<ProductEntity> findByIdAndTrader_Id(Long productId, Long traderId);

    List<ProductEntity> findAllByTraderIdAndCategoryId(Long traderId, Long categoryId);

    boolean existsByIdAndTrader_Id(Long productId, Long traderId);
}