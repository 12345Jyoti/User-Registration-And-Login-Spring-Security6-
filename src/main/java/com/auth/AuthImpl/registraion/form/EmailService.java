//package com.auth.AuthImpl.registraion.form;
//
//import com.auth.AuthImpl.registraion.dtos.fields.EmailField;
//import com.auth.AuthImpl.registraion.enums.FormMedium;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    public RegisterForm buildEmailForm() {
//        EmailField emailField = createEmailField();
//
//        return new RegisterForm.Builder()
//                .withField(FormMedium.EMAIL, emailField)
//                .withMessage("Please enter your email to proceed.")
//                .build();
//    }
//
//    private EmailField createEmailField() {
//        EmailField emailField = new EmailField();
//        emailField.setInputType("email");
//        emailField.setMandatory(true);
//        emailField.setValidationType("true");
//        emailField.setPlaceHolder("Enter your email");
//        return emailField;
//    }
//}
