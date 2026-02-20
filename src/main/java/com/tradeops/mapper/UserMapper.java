package com.tradeops.mapper;

import com.tradeops.models.dto.UserDTO;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "fullName", target = "fullName")
    UserResponse toUserResponse(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", expression = "java(dto.getEmail())")
    UserEntity toEntity(UserDTO dto);
}
