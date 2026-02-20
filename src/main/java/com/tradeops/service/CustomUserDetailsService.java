package com.tradeops.service;

import com.tradeops.dataservices.UserEntityDataService;
import com.tradeops.models.entity.Role;
import com.tradeops.models.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserEntityDataService userEntityDataService;

    public CustomUserDetailsService(UserEntityDataService userEntityDataService) {
      this.userEntityDataService = userEntityDataService;
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
      return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      UserEntity user = userEntityDataService.getUserEntityByUsernameOrThrow(username);

      List<String> roleNames = user.getRoles().stream()
        .map(Role::getName)
        .toList();

      System.out.println("User found: " + user.getUsername() + " with roles: " + roleNames);

      return new User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }
}
