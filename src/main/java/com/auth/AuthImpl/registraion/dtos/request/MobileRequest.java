package com.auth.AuthImpl.registraion.dtos.request;

public class MobileRequest {
    private String isdCode;     // ISD code (e.g., "91")
    private String phoneNumber; // Mobile number

    // Getters and Setters
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
}

