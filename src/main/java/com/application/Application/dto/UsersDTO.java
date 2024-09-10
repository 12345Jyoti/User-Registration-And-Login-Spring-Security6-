package com.application.Application.dto;

import com.application.Application.common.enums.Roles;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one uppercase letter, one special character, and no spaces")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9 ]{10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Email verification status is required")
    private boolean isEmailVerified;

    @NotNull(message = "Phone number verification status is required")
    private boolean isPhoneNumberVerified;

    private Set<Roles> roles;

    @NotBlank(message = "Country is required")
    private String country;

    private String emailOtp;
    private String phoneOtp;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
