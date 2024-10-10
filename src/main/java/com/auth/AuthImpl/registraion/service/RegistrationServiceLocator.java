package com.auth.AuthImpl.registraion.service;

import com.auth.AuthImpl.registraion.enums.Medium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceLocator {

    @Autowired
    private MobileRegistrationService mobileRegistrationService;

    @Autowired
    private EmailRegistrationService emailRegistrationService;

    public RegistrationInterface getRegistrationService(Medium medium) {
        switch (medium) {
            case MOBILE:
                return mobileRegistrationService;
            case EMAIL:
                return emailRegistrationService;
            default:
                throw new IllegalArgumentException("Invalid registration medium");
        }
    }
}
