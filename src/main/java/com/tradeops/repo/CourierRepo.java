package com.tradeops.repo;

import com.tradeops.models.entity.Courier;
import com.tradeops.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepo extends JpaRepository<Courier, Long> {
    Optional<Courier> findCourierByUserEntity(UserEntity userEntity);


}