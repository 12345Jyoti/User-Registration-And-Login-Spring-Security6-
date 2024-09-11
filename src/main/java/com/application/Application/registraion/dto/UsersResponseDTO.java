package com.application.Application.registraion.dto;

import com.application.Application.registraion.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean isEmailVerified;
    private boolean isPhoneNumberVerified;
    private Set<Roles> roles;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
