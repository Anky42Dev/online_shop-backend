package com.tradeops.repo;

import com.tradeops.models.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // ─────────────────────────────────────────────────────────────
    // LIST версия (расширенный поиск + description)
    // ─────────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM ProductEntity p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:traderId   IS NULL OR p.trader.id   = :traderId)
              AND (:search     IS NULL
                   OR LOWER(p.name)        LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    List<ProductEntity> findAllWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("traderId")   Long traderId,
            @Param("search")     String search
    );

    // ─────────────────────────────────────────────────────────────
    // PAGE версия (для пагинации + упрощённый search)
    // ─────────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM ProductEntity p
            WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:traderId   IS NULL OR p.trader.id   = :traderId)
              AND (:search     IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<ProductEntity> findAllWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("traderId")   Long traderId,
            @Param("search")     String search,
            Pageable pageable
    );
}