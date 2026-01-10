package com.shop.onlineshop.repo;

import com.shop.onlineshop.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;

@Repository
public interface UserEntityRepo extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
  Optional<UserEntity> findByEmail(String email);
  Optional<UserEntity> findById(Long id);

  boolean existsByRoles_Name(String name);

  UserEntity save(UserEntity userEntity);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);


}
