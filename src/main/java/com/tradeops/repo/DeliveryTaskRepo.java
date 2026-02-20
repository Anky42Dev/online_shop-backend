package com.tradeops.repo;

import com.tradeops.models.entity.DeliveryTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepo extends JpaRepository<DeliveryTaskEntity, Long> {
    List<DeliveryTaskEntity> findDeliveryTaskEntitiesByCourier_Id(Long courierId);
}
