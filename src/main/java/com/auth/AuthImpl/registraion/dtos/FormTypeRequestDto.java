package com.auth.AuthImpl.registraion.dtos;


import com.auth.AuthImpl.registraion.enums.FormType;

public class FormTypeRequestDto {
    private FormType formType = FormType.SIGNUP;  // Default value

    // Getter and Setter
    public FormType getFormType() {
        return formType;
    }

    public void setFormType(FormType formType) {
        this.formType = formType;
    }
}

