package com.application.Application.registraion.dto;

import com.application.Application.registraion.enums.Roles;
import jakarta.validation.constraints.*;
import java.util.Set;

public class UsersRequestDTO {

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


    public UsersRequestDTO(){}

    public UsersRequestDTO(String username, String password, String email, String phoneNumber, boolean isEmailVerified, boolean isPhoneNumberVerified, Set<Roles> roles, String country, String emailOtp, String phoneOtp) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isEmailVerified = isEmailVerified;
        this.isPhoneNumberVerified = isPhoneNumberVerified;
        this.roles = roles;
        this.country = country;
        this.emailOtp = emailOtp;
        this.phoneOtp = phoneOtp;
    }

    public @NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username) {
        this.username = username;
    }

    public @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one uppercase letter, one special character, and no spaces") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one uppercase letter, one special character, and no spaces") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9 ]{10}$", message = "Invalid phone number format") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9 ]{10}$", message = "Invalid phone number format") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NotNull(message = "Email verification status is required")
    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(@NotNull(message = "Email verification status is required") boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    @NotNull(message = "Phone number verification status is required")
    public boolean isPhoneNumberVerified() {
        return isPhoneNumberVerified;
    }

    public void setPhoneNumberVerified(@NotNull(message = "Phone number verification status is required") boolean phoneNumberVerified) {
        isPhoneNumberVerified = phoneNumberVerified;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public @NotBlank(message = "Country is required") String getCountry() {
        return country;
    }

    public void setCountry(@NotBlank(message = "Country is required") String country) {
        this.country = country;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public String getPhoneOtp() {
        return phoneOtp;
    }

    public void setPhoneOtp(String phoneOtp) {
        this.phoneOtp = phoneOtp;
    }
}
