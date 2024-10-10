//package com.auth.AuthImpl.utils.config;
//
//
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TwilioService {
//
//    @Autowired
//    private TwilioConfig twilioConfig;
//
//    /**
//     * Sends an OTP message via Twilio SMS
//     *
//     * @param toPhoneNumber, The recipient's phone number
//     * @param isdCode, The recipient's isdCode
//     * @param otp           The OTP to send
//     */
//    // call sendSms and tracking
//
//    //private
//    public void sendSms(String isdCode, String toPhoneNumber, String otp) {
//        try {
//            String fullPhoneNumber = isdCode + toPhoneNumber;
//            if (!fullPhoneNumber.startsWith("+")) {
//                fullPhoneNumber = "+" + fullPhoneNumber;
//            }
//
//            // Send SMS via Twilio
//            Message.creator(
//                            new PhoneNumber(fullPhoneNumber),
//                            new PhoneNumber(twilioConfig.getFromPhoneNumber()),
//                            "Your OTP is: " + otp)   // Message content
//                    .create();
//            System.out.println("OTP sent successfully to: " + fullPhoneNumber);
//        } catch (Exception e) {
////            System.err.println("Failed to send OTP to: " + fullPhoneNumber + " due to: " + e.getMessage());
//            throw new RuntimeException("Failed to send SMS via Twilio", e);
//        }
//    }
//
//
//}
//
package com.auth.AuthImpl.utils.config;

import com.auth.AuthImpl.registraion.entity.SmsTracking;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.entity.OtpVerification; // Ensure this import is present
import com.auth.AuthImpl.registraion.repo.SmsTrackingRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TwilioService {

    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private SmsTrackingRepository smsTrackingRepository; // Inject the repository

    /**
     * Sends an OTP message via Twilio SMS and tracks the attempt.
     *
     * @param user           The recipient's user entity
     * @param otpVerification The OTP verification entity (can be null)
     * @param isdCode       The recipient's ISD code
     * @param toPhoneNumber  The recipient's phone number
     * @param otp            The OTP to send
     */
    public void sendSms(Users user, OtpVerification otpVerification, String isdCode, String toPhoneNumber, String otp) {
        String fullPhoneNumber = formatPhoneNumber(isdCode, toPhoneNumber);

        try {
            sendOtpMessage(fullPhoneNumber, otp);
            trackSmsAttempt(user, otpVerification);
        } catch (Exception e) {
            logSmsSendingError(fullPhoneNumber, e);
            throw new RuntimeException("Failed to send SMS via Twilio", e);
        }
    }

    /**
     * Formats the phone number by ensuring it starts with a "+".
     *
     * @param isdCode      The ISD code
     * @param toPhoneNumber The recipient's phone number
     * @return The formatted full phone number
     */
    private String formatPhoneNumber(String isdCode, String toPhoneNumber) {
        return ("+" + isdCode + toPhoneNumber).replaceAll(" ", "");
    }

    /**
     * Sends the OTP message via Twilio.
     *
     * @param fullPhoneNumber The full formatted phone number
     * @param otp             The OTP to send
     */
    private void sendOtpMessage(String fullPhoneNumber, String otp) {
        Message.creator(
                        new PhoneNumber(fullPhoneNumber),
                        new PhoneNumber(twilioConfig.getFromPhoneNumber()),
                        "Your OTP is: " + otp)   // Message content
                .create();

        System.out.println("OTP sent successfully to: " + fullPhoneNumber);
    }

    /**
     * Tracks the SMS attempt in the SmsTracking table.
     *
     * @param user           The user entity
     * @param otpVerification The OTP verification entity (can be null)
     */
    private void trackSmsAttempt(Users user, OtpVerification otpVerification) {
        SmsTracking smsTracking = new SmsTracking();
        smsTracking.setUser(user);
        smsTracking.setOtpVerification(otpVerification);
        smsTracking.setAttempts(1); // Set attempts to 1 for the first send
        smsTracking.setCreatedBy("USER");
        smsTracking.setLastAttemptAt(LocalDateTime.now());

        smsTrackingRepository.save(smsTracking);
    }

    /**
     * Logs the error that occurred while sending the SMS.
     *
     * @param fullPhoneNumber The phone number to which the SMS was sent
     * @param e               The exception that occurred
     */
    private void logSmsSendingError(String fullPhoneNumber, Exception e) {
        System.err.println("Failed to send OTP to: " + fullPhoneNumber + " due to: " + e.getMessage());
    }
}
