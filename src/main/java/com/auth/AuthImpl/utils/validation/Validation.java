package com.auth.AuthImpl.utils.validation;



import com.auth.AuthImpl.registraion.entity.OtpVerification;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class Validation {


    @Autowired
    private UserRepository userRepository;

    private static final int MAX_ATTEMPTS = 5;

    /**
     * Validates that the user has valid identification (phone or email).
     */
    public void validateUser(Users user) {
        if (user == null || (user.getPhoneNumber() == null && user.getEmail() == null)) {
            throw new IllegalArgumentException("User must have at least a phone number or an email.");
        }
    }

    /**
     * Validates the OTP has not been used, has not expired, and that the number of attempts has not exceeded the maximum.
     */
    public void verifyOtpValidity(OtpVerification otpVerification) {
        if (otpVerification.isUsed()) {
            throw new IllegalArgumentException("OTP has already been used.");
        }

        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired.");
        }

        if (otpVerification.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Maximum OTP attempts exceeded.");
        }
    }

    /**
     * Validates that an OTP can be resent, checking wait time and max resend attempts.
     */
    public void validateResendOtp(OtpVerification otpVerification) {
        long waitTime = calculateWaitTime(otpVerification.getAttempts());
        LocalDateTime nextSendTime = otpVerification.getCreatedAt().plusMinutes(waitTime);

        if (LocalDateTime.now().isBefore(nextSendTime)) {
            throw new IllegalArgumentException("You must wait before resending the OTP.");
        }

        if (otpVerification.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Cannot resend OTP. Maximum resend attempts reached.");
        }
    }

    /**
     * Finds a user based on the provided medium (phone/email).
     */
    public Users findUserByMedium(Users user, Medium medium) {
        if (medium == Medium.MOBILE) {
            return userRepository.findByPhoneNumber(user.getPhoneNumber())
                    .orElseThrow(() -> new IllegalArgumentException("User not found for phone number: " + user.getPhoneNumber()));
        } else if (medium == Medium.EMAIL) {
            return userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + user.getEmail()));
        }
        throw new IllegalArgumentException("Invalid medium provided.");
    }

    /**
     * Calculate the waiting time before the OTP can be resent based on the number of attempts.
     */
    public long calculateWaitTime(int attempts) {
        switch (attempts) {
            case 0: return 1;  // 1 minute
            case 1: return 2;  // 2 minutes
            case 2: return 5;  // 5 minutes
            case 3: return 10; // 10 minutes
            case 4: return 60; // 60 minutes
            default: throw new IllegalArgumentException("Maximum resend attempts reached.");
        }
    }
}
