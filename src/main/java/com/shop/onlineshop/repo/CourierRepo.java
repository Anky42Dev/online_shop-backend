package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.Courier;
import com.shop.onlineshop.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepo extends JpaRepository<Courier, Long> {
    Courier findCourierByUserEntity(UserEntity userEntity);
}
