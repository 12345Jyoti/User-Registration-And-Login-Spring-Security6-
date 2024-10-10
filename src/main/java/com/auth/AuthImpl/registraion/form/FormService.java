package com.auth.AuthImpl.registraion.form;

import com.auth.AuthImpl.registraion.enums.FormType;

public interface FormService {
    RegisterForm buildFormResponse();
    FormType getFormType();
}

