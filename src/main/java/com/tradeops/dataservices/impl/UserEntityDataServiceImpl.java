package com.tradeops.dataservices.impl;

import com.tradeops.dataservices.UserEntityDataService;
import com.tradeops.exceptions.UserAlreadyExistsException;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.repo.UserEntityRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserEntityDataServiceImpl implements UserEntityDataService {
  private final UserEntityRepo userEntityRepo;
  public UserEntityDataServiceImpl(UserEntityRepo userEntityRepo) {
    this.userEntityRepo = userEntityRepo;
  }

  @Override
  @Transactional(readOnly = true)
  public UserEntity getUserEntityByEmailOrThrow(String email) {
    if(email == null || email.isEmpty()){
      throw new IllegalArgumentException("Email cannot be null or empty");
    }
    if(userEntityRepo.findByEmail(email).isPresent()){
      return userEntityRepo.findByEmail(email).get();
    }
    throw new EntityNotFoundException("User not found");
  }

  @Override
  @Transactional(readOnly = true)
  public UserEntity getUserEntityByUsernameOrThrow(String username) {
    if(username == null || username.isEmpty()){
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if(userEntityRepo.findByUsername(username).isPresent()){
      return userEntityRepo.findByUsername(username).get();
    }
    throw new EntityNotFoundException("User not found");
  }

  @Override
  @Transactional()
  public void saveUserEntity(UserEntity userEntity) {
    if(existsByUsername(userEntity.getUsername())){
      throw new UserAlreadyExistsException("Username: " + userEntity.getUsername() + " already exists");
    }
    if(existsByEmail(userEntity.getEmail())){
      throw new UserAlreadyExistsException("Email:" + userEntity.getEmail() + " already exists");
    }
    if(!userEntity.getEmail().contains("@")) throw new IllegalArgumentException("Invalid email format");
    userEntityRepo.save(userEntity);
  }

  @Override
  @Transactional
  public void updateUserEntity(UserEntity userEntity) {
    userEntityRepo.save(userEntity);
  }


  @Override
  @Transactional(readOnly = true)
  public UserEntity getUserEntityByIdOrThrow(Long id) {
    if(id == null){
      throw new IllegalArgumentException("Id cannot be null");
    }
    if(userEntityRepo.findById(id).isPresent()){
      return userEntityRepo.findById(id).get();
    }
    throw new EntityNotFoundException("User not found");
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    if(username == null || username.isEmpty()){
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return userEntityRepo.existsByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    if(email == null || email.isEmpty()){
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return userEntityRepo.existsByEmail(email);
  }
}
