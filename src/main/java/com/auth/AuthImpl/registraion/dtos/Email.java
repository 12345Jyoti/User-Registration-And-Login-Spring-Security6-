package com.auth.AuthImpl.registraion.dtos;

import com.auth.AuthImpl.registraion.dtos.fields.EmailField;

public class Email {
    private EmailField emailField;

    public EmailField getEmailField() {
        return emailField;
    }

    public void setEmailField(EmailField emailField) {
        this.emailField = emailField;
    }

    public Email() {
    }

    public Email(EmailField emailField) {
        this.emailField = emailField;
    }
}
