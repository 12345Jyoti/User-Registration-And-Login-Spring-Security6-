package com.auth.AuthImpl.registraion.dtos;

public class Email extends AbstractField<String> {
    private String emailAddress;

    // Getters and Setters
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
