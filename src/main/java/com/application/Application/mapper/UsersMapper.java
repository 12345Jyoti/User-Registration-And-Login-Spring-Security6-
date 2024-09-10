package com.application.Application.mapper;

import com.application.Application.dto.UsersDTO;
import com.application.Application.entity.Role;
import com.application.Application.entity.Users;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsersMapper {

    public static UsersDTO entityToDto(Users user) {
        if (user == null) {
            return null;
        }

        UsersDTO dto = new UsersDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmailOtp(user.getEmailOtp());
        dto.setPhoneOtp(user.getPhoneOtp());
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

    public static Users dtoToEntity(UsersDTO dto) {
        if (dto == null) {
            return null;
        }

        Users user = new Users();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setEmailOtp(dto.getEmailOtp());
        user.setPhoneOtp(dto.getPhoneOtp());
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
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());

        return user;
    }
}
