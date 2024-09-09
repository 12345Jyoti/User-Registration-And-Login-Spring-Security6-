package com.application.Application.otpServices;


import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendOtp(String phoneNumber, String otp) {
        // Simulate sending SMS OTP (could be replaced with real SMS service, e.g., Twilio)
        System.out.println("Sending OTP to phone number: " + phoneNumber + ". OTP: " + otp);
    }
}

