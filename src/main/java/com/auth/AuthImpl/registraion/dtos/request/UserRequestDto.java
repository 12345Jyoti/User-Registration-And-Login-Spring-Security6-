package com.auth.AuthImpl.registraion.dtos.request;

import com.auth.AuthImpl.registraion.enums.Medium;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class UserRequestDto {

    @NotNull(message = "ID is required.")
    private Long id; // Unique identifier for the user request

    @NotNull(message = "Medium is required.")
    private Medium medium;  // Either PHONE or EMAIL

    // Fields for phone sign-up
    @Pattern(regexp = "^\\+?[0-9]{1,4}$", message = "Invalid ISD code.")
    private String isdCode;

    @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Invalid phone number.")
    private String phoneNumber;

    // Fields for email sign-up
    @Email(message = "Invalid email address.")
    private String email;

    private String otp; // OTP for login

    // Timestamps for creation and update
    private LocalDateTime createdAt; // Timestamp of creation
    private LocalDateTime updatedAt; // Timestamp of the last update

    // Users who created or updated the record
    private String createdBy; // User who created the record
    private String updatedBy; // User who last updated the record

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public String getIsdCode() {
        return isdCode;
    }

    public void setIsdCode(String isdCode) {
        this.isdCode = isdCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
