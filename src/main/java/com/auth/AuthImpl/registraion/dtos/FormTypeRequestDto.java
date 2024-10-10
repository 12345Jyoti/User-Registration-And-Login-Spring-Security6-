package com.auth.AuthImpl.registraion.dtos;


import com.auth.AuthImpl.registraion.enums.FormType;

public class FormTypeRequestDto {
    private FormType formType = FormType.SIGNUP;

    private int version = 1;

    public FormType getFormType() {
        return formType;
    }

    public void setFormType(FormType formType) {
        this.formType = formType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
