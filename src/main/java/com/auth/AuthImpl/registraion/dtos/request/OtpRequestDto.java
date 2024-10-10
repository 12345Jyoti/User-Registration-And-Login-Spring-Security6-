package com.auth.AuthImpl.registraion.dtos.request;

import com.auth.AuthImpl.registraion.enums.Medium;

public class OtpRequestDto {

    private String userIdentifier;
    private String otp;
    private Medium medium;

    // Constructors
    public OtpRequestDto() {
    }

    public OtpRequestDto(String userIdentifier, String otp, Medium medium) {
        this.userIdentifier = userIdentifier;
        this.otp = otp;
        this.medium = medium;
    }

    // Getters and Setters
    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }
}

