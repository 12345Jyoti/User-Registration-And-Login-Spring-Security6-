package com.application.Application.otpServices;



import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendOtp(String email, String otp) {
        // Simulate sending email OTP (could be replaced with real email service, e.g., SendGrid)
        System.out.println("Sending OTP to email: " + email + ". OTP: " + otp);
    }
}

