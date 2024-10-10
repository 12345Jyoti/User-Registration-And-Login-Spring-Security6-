package com.auth.AuthImpl.registraion.form;

import com.auth.AuthImpl.registraion.dtos.fields.EmailField;
import com.auth.AuthImpl.registraion.dtos.fields.IsdField;
import com.auth.AuthImpl.registraion.dtos.fields.MobileField;
import com.auth.AuthImpl.registraion.enums.FormMedium;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FormBuilderService {   //FormBuilder service


    public RegisterForm buildMobileForm() {
        IsdField isdField = createIsdField();
        MobileField mobileField = createMobileField();

        return new RegisterForm.Builder()
                .withField(FormMedium.ISD_CODE, isdField)
                .withField(FormMedium.PHONE_NUMBER, mobileField)
                .withMessage("Please enter your mobile number to proceed.")
                .build();
    }


    private IsdField createIsdField() {
        IsdField isdField = new IsdField();
        isdField.setInputType("isdCode");
        isdField.setOptions(List.of("+1", "+91", "+44"));
        isdField.setMandatory(true);
        isdField.setPlaceHolder("Select your country code");
        isdField.setValidationType("true");
        return isdField;
    }

    private MobileField createMobileField() {
        MobileField mobileField = new MobileField();
        mobileField.setInputType("number");
        mobileField.setMandatory(true);
        mobileField.setPlaceHolder("Enter your mobile number");
        mobileField.setValidationType("true");
        return mobileField;
    }

    public RegisterForm buildEmailForm() {
        EmailField emailField = createEmailField();

        return new RegisterForm.Builder()
                .withField(FormMedium.EMAIL, emailField)
                .withMessage("Please enter your email to proceed.")
                .build();
    }

    private EmailField createEmailField() {
        EmailField emailField = new EmailField();
        emailField.setInputType("email");
        emailField.setMandatory(true);
        emailField.setValidationType("true");
        emailField.setPlaceHolder("Enter your email");
        return emailField;
    }
}
