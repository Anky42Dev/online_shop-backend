package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.DeliveryTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepo extends JpaRepository<DeliveryTaskEntity, Long> {
    List<DeliveryTaskEntity> findDeliveryTaskEntitiesByCourier_Id(Long courierId);
}
