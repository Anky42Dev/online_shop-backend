package com.tradeops.dataservices.impl;

import com.tradeops.dataservices.RoleDataService;
import com.tradeops.models.entity.Role;
import com.tradeops.repo.RoleRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RoleDataServiceImpl implements RoleDataService {
  private final RoleRepo roleRepo;

  public RoleDataServiceImpl(RoleRepo roleRepo) {
    this.roleRepo = roleRepo;
  }

  @Override
  public Role findByName(String name) {
    if(name == null) throw new IllegalArgumentException("Role name cannot be null");
    return roleRepo.findByName(name).orElseThrow(()-> new EntityNotFoundException("Role not found"));
  }
}
