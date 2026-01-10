package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByCustomerEntity_UserEntity_Username(String username);
}