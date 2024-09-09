package com.application.Application.dto;

import com.application.Application.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Email verification status is required")
    private boolean isEmailVerified;

    @NotNull(message = "Phone number verification status is required")
    private boolean isPhoneNumberVerified;

    private Set<Role> roles;

    @NotBlank(message = "Country is required")
    private String country;


    private String emailOtp;
    private String phoneOtp;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
