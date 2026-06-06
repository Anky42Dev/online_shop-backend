package com.tradeops.repo;

import com.tradeops.models.entity.OrderEntity;
import com.tradeops.models.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

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
}