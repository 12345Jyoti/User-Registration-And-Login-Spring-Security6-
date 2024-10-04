package com.auth.AuthImpl.utils.config;


import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Autowired
    private TwilioConfig twilioConfig;

    /**
     * Sends an OTP message via Twilio SMS
     *
     * @param toPhoneNumber The recipient's phone number
     * @param otp           The OTP to send
     */
    public void sendSms(String toPhoneNumber, String otp) {
        try {
            Message.creator(
                            new PhoneNumber(toPhoneNumber),    // To
                            new PhoneNumber(twilioConfig.getFromPhoneNumber()), // From
                            "Your OTP is: " + otp)   // Message content
                    .create();
            System.out.println("OTP sent successfully to: " + toPhoneNumber);
        } catch (Exception e) {
            System.err.println("Failed to send OTP to: " + toPhoneNumber + " due to: " + e.getMessage());
            throw new RuntimeException("Failed to send SMS via Twilio", e);
        }
    }
}

