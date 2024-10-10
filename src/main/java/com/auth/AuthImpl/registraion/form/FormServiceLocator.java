package com.auth.AuthImpl.registraion.form;

import com.auth.AuthImpl.registraion.enums.FormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormServiceLocator {

    private final Map<FormType, FormService> formServiceMap;

    @Autowired
    public FormServiceLocator(List<FormService> formServices) {
        this.formServiceMap = formServices.stream()
                .collect(Collectors.toMap(FormService::getFormType, service -> service));
    }

    public FormService getService(FormType formType) {
        FormService service = formServiceMap.get(formType);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported form type: " + formType);
        }
        return service;
    }
}
