package com.auth.AuthImpl.registraion.otpServices;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendOtp(String email, String otp) {
        logger.info("Sending OTP to email: {}. OTP: {}", email, otp);    }
}

