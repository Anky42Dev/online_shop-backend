package com.tradeops.repo;

import com.tradeops.models.dto.TopProductDto;
import com.tradeops.models.entity.OrderEntity;
import com.tradeops.models.model.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findAllByCustomerEntity_UserEntity_Username(String username);

    @Query("""
            SELECT COUNT(o) > 0 FROM OrderEntity o
            JOIN o.orderItems i
            WHERE i.product.id = :productId
              AND o.status NOT IN (:terminalStatuses)
            """)
    boolean existsActiveOrdersByProductId(
            @Param("productId") Long productId,
            @Param("terminalStatuses") List<OrderStatus> terminalStatuses);

    List<OrderEntity> findAllByTrader_Id(Long traderId);

    List<OrderEntity> findAllByTrader_IdAndStatus(Long traderId, OrderStatus status);

    Optional<OrderEntity> findByIdAndTrader_Id(Long orderId, Long traderId);

    // ── BE-014: Analytics ─────────────────────────────────────────────────────

    @Query("""
            SELECT COALESCE(SUM(o.totalPrice), 0)
            FROM OrderEntity o
            WHERE o.trader.id = :traderId
              AND o.status = 'DELIVERED'
              AND o.createdAt BETWEEN :from AND :to
            """)
    BigDecimal getTotalRevenue(
            @Param("traderId") Long traderId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT COUNT(o)
            FROM OrderEntity o
            WHERE o.trader.id = :traderId
              AND o.status = 'DELIVERED'
              AND o.createdAt BETWEEN :from AND :to
            """)
    Long countOrders(
            @Param("traderId") Long traderId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT new com.tradeops.models.dto.TopProductDto(
                oi.product.id,
                oi.product.name,
                SUM(oi.quantity),
                SUM(oi.quantity * oi.currentPrice)
            )
            FROM OrderEntity o
            JOIN o.orderItems oi
            WHERE o.trader.id = :traderId
              AND o.status = 'DELIVERED'
              AND o.createdAt BETWEEN :from AND :to
            GROUP BY oi.product.id, oi.product.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<TopProductDto> getTopProducts(
            @Param("traderId") Long traderId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}