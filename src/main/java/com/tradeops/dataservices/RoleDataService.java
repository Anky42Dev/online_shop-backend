package com.tradeops.dataservices;

import com.tradeops.models.entity.Role;

public interface RoleDataService {
  Role findByName(String name);


}
