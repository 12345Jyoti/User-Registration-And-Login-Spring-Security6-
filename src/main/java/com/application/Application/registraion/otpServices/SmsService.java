package com.application.Application.registraion.otpServices;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendOtp(String phoneNumber, String otp) {
        logger.info("Sending OTP to phoneNumber: {}. OTP: {}", phoneNumber, otp);    }
}

