package com.shop.onlineshop.config;

import com.shop.onlineshop.models.entity.Role;
import com.shop.onlineshop.models.entity.UserEntity;
import com.shop.onlineshop.repo.RoleRepo;
import com.shop.onlineshop.repo.UserEntityRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

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
    var role_user = roleRepo.findByName("ROLE_USER").orElseGet(() -> {
      var r = new Role();
      r.setName("ROLE_USER");
      return roleRepo.save(r);
    });
    var role_admin = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> {
      var r = new Role();
      r.setName("ROLE_ADMIN");
      return roleRepo.save(r);
    });
    var role_customer = roleRepo.findByName("ROLE_CUSTOMER").orElseGet(() -> {
      var r = new Role();
      r.setName("ROLE_CUSTOMER");
      return roleRepo.save(r);
    });
    var role_trader = roleRepo.findByName("ROLE_TRADER").orElseGet(() -> {
      var r = new Role();
      r.setName("ROLE_TRADER");
      return roleRepo.save(r);
    });
    var role_courier = roleRepo.findByName("ROLE_COURIER").orElseGet(() -> {
      var r = new Role();
      r.setName("ROLE_COURIER");
      return roleRepo.save(r);
    });
    if (roleRepo.findByName("ROLE_TRADER").isEmpty()) roleRepo.save(new Role("ROLE_TRADER"));
    if (roleRepo.findByName("ROLE_COURIER").isEmpty()) roleRepo.save(new Role("ROLE_COURIER"));
    if (roleRepo.findByName("ROLE_CUSTOMER").isEmpty()) roleRepo.save(new Role("ROLE_CUSTOMER"));
    boolean isAdmin = userRepo.existsByRoles_Name("ROLE_ADMIN");
    if (!isAdmin) {
      if (!userRepo.existsByUsername(ADMIN_USERNAME)) {
        var user = new UserEntity();
        user.setUsername(ADMIN_USERNAME);
        user.setEmail("okuulib.admin@okuulib.com");
        user.setFullName("System Administrator");
        user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        user.setRoles(new ArrayList<>(Collections.singletonList(role_admin)));
        user.setCreatedAt(LocalDateTime.now());
        // Admin should be considered verified and not require OTP
        user.setVerified(true);
        user.setOtpEnabled(false);
        userRepo.save(user);
      } else {
        var user = userRepo.findByUsername(ADMIN_USERNAME).orElseThrow();
        if (user.getRoles().stream().noneMatch(r -> "ROLE_ADMIN".equals(r.getName()))) {
          var roles = new ArrayList<>(user.getRoles());
          roles.add(role_admin);
          user.setRoles(roles);
        }
        // Ensure existing admin user is also marked as verified and OTP disabled
        user.setVerified(true);
        user.setOtpEnabled(false);
        userRepo.save(user);
      }
    }
  }
}

