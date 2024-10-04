package com.auth.AuthImpl.registraion.dtos;

import java.util.List;

public class Mobile extends AbstractField<String> {
    private String isdCode;     // Directly store the ISD code
    private String phoneNumber;  // Directly store the phone number

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
    }}

