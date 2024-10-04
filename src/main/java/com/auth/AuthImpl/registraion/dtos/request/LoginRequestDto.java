package com.auth.AuthImpl.registraion.dtos.request;

import com.auth.AuthImpl.registraion.dtos.Email;
import com.auth.AuthImpl.registraion.dtos.Mobile;
import com.auth.AuthImpl.registraion.enums.Medium;

import java.util.List;

public class LoginRequestDto {
    private List<Medium> medium; // e.g., [mobile, email, google, truecaller]
    private Mobile mobile; // Mobile fields
    private Email email; // Email fields

    // Getters and Setters
    public List<Medium> getMedium() {
        return medium;
    }

    public void setOptions(List<Medium> options) {
        this.medium = options;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
}

