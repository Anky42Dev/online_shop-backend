package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<CartEntity, Long>{
    Optional<CartEntity> findByCustomer_Id(Long customerId);
}
