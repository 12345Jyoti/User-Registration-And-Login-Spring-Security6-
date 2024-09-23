package com.auth.AuthImpl.registraion.mapper;

import com.auth.AuthImpl.registraion.dto.UsersRequestDTO;
import com.auth.AuthImpl.registraion.dto.UsersResponseDTO;
import com.auth.AuthImpl.registraion.entity.Role;
import com.auth.AuthImpl.registraion.entity.Users;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsersMapper {

    public static UsersResponseDTO entityToResponseDto(Users user) {
        if (user == null) {
            return null;
        }

        UsersResponseDTO dto = new UsersResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setPhoneNumberVerified(user.isPhoneNumberVerified());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet()));
        dto.setCountry(user.getCountry());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    public static Users requestDtoToEntity(UsersRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmailVerified(dto.isEmailVerified());
        user.setPhoneNumberVerified(dto.isPhoneNumberVerified());

        Set<Role> roles = dto.getRoles().stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
        user.setRoles(roles);

        user.setCountry(dto.getCountry());

        return user;
    }
}
