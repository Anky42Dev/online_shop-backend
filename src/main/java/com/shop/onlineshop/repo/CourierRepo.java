package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.Courier;
import com.shop.onlineshop.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepo extends JpaRepository<Courier, Long> {
    Optional<Courier> findCourierByUserEntity(UserEntity userEntity);


}