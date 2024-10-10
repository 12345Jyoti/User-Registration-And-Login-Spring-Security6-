package com.auth.AuthImpl.registraion.form;
import com.auth.AuthImpl.registraion.enums.FormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FormResponseService {

    @Autowired
    private FormServiceLocator formServiceLocator;

    public RegisterForm getRegistrationForm(FormType formType) {
        FormService formService = formServiceLocator.getService(formType);
        return formService.buildFormResponse();
    }
}
