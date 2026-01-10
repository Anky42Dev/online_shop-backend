package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<CustomerEntity,Long> {
    Optional<CustomerEntity> findByUserEntity_Username(String username);
}
