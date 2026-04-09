package com.tradeops.config;

import com.tradeops.models.entity.Role;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.repo.RoleRepo;
import com.tradeops.repo.UserEntityRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RoleSeeder implements CommandLineRunner {
  private final PasswordEncoder passwordEncoder;
  private final UserEntityRepo userRepo;
  private final RoleRepo roleRepo;

  @Value("${onlineshop.app.admin.username}")
  private String ADMIN_USERNAME;

  @Value("${onlineshop.app.admin.password}")
  private String ADMIN_PASSWORD;

  public RoleSeeder(RoleRepo roleRepo,
                    UserEntityRepo userRepo,
                    PasswordEncoder passwordEncoder){
    this.roleRepo = roleRepo;
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    List<String> roles = Arrays.asList(
            "ROLE_USER",
            "ROLE_ADMIN",       // Super Admin / Catalog Manager
            "ROLE_DISPATCHER",  // Логист, назначает курьеров
            "ROLE_OPS",          // Склад (Warehouse), меняет статус на READY_FOR_PICKUP
            "ROLE_COURIER",     // Доставка
            "ROLE_TRADER",      // Админ магазина (Storefront Admin)
            "ROLE_CUSTOMER"     // (Deprecated в новой схеме, но пусть будет)
    );

    for (String roleName : roles) {
      createRoleIfNotFound(roleName);
    }

    Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow();

    userRepo.findByUsername(ADMIN_USERNAME).ifPresentOrElse(
            user -> {
              boolean hasAdminRole = user.getRoles().stream()
                      .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

              if (!hasAdminRole) {
                user.getRoles().add(adminRole);
                userRepo.save(user);
              }
            },
            () -> {
              var user = new UserEntity();
              user.setUsername(ADMIN_USERNAME);
              user.setEmail("admin@tradeops.com");
              user.setFullName("Super Administrator");
              user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
              user.setRoles(new ArrayList<>(Collections.singletonList(adminRole)));
              user.setCreatedAt(LocalDateTime.now());
              user.setVerified(true);
              user.setRejected(false);
              user.setApproved(true);
              user.setActive(true);
              userRepo.save(user);
              System.out.println("✅ Super Admin created: " + ADMIN_USERNAME);
            }
    );
  }

  private void createRoleIfNotFound(String name) {
    if (roleRepo.findByName(name).isEmpty()) {
      roleRepo.save(new Role(name));
      System.out.println("Created role: " + name);
    }
  }
}