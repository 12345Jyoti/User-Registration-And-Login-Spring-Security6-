package com.auth.AuthImpl.registraion.form;

import com.auth.AuthImpl.registraion.enums.FormType;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.auth.AuthImpl.utils.RegistrationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService implements FormService {

    @Value("${registration.options.active}")
    private List<Medium> activeOptions;

    private RegisterForm mobileFormCache = null;
    private RegisterForm emailFormCache = null;

    @Autowired
    private FormBuilderService formBuilderService;


    @Override
    public RegisterForm buildFormResponse() {
        return buildSignupFormResponse();
    }

    @Override
    public FormType getFormType() {
        return FormType.SIGNUP;
    }


    private RegisterForm buildSignupFormResponse() {
        Medium primaryActiveOption = RegistrationUtils.getPrimaryActiveOption(activeOptions);
        return createRegisterForm(primaryActiveOption);
    }

    private RegisterForm createRegisterForm(Medium primaryMedium) {
        switch (primaryMedium) {
            case MOBILE:
                if (mobileFormCache != null) {
                    return mobileFormCache;
                }
                mobileFormCache = formBuilderService.buildMobileForm();
                return mobileFormCache;

            case EMAIL:
                if (emailFormCache != null) {
                    return emailFormCache;
                }
                emailFormCache = formBuilderService.buildEmailForm();
                return emailFormCache;

            default:
                throw new IllegalArgumentException("Invalid signup option: " + primaryMedium);
        }
    }


}

