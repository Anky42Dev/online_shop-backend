package com.tradeops.repo;

import com.tradeops.models.entity.DeliveryTaskEntity;
import com.tradeops.models.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepo extends JpaRepository<DeliveryTaskEntity, Long> {

    List<DeliveryTaskEntity> findDeliveryTaskEntitiesByCourier_Id(Long courierId);

    // BE-016: подсчёт активных задач курьера (исключая терминальные статусы)
    long countByCourier_IdAndOrderStatusNotIn(Long courierId, List<OrderStatus> excludedStatuses);
}