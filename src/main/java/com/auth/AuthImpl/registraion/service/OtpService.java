//package com.auth.AuthImpl.registraion.service;
//
//import com.auth.AuthImpl.registraion.entity.SmsTracking;
//import com.auth.AuthImpl.registraion.entity.OtpVerification;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.enums.Medium;
//import com.auth.AuthImpl.registraion.enums.OtpType;
//import com.auth.AuthImpl.registraion.enums.Status;
//import com.auth.AuthImpl.registraion.repo.SmsTrackingRepository;
//import com.auth.AuthImpl.registraion.repo.OtpVerificationRepository;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import com.auth.AuthImpl.utils.JWTService;
//import com.auth.AuthImpl.utils.config.TwilioService;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.http.HttpHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//
//@Service
//public class OtpService {
//
//    @Autowired
//    private OtpVerificationRepository otpVerificationRepository;
//
//    @Autowired
//    private SmsTrackingRepository smsTrackingRepository;
//
//    @Autowired
//    private TwilioService twilioService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private JWTService jwtService;
//
//    public String generateOtp() {
//        return String.valueOf((int) (Math.random() * 900000) + 100000); // Example: random 6-digit OTP
//    }
//
//    public OtpVerification saveOtpVerification(Users user, String otp, OtpType otpType) {
//        OtpVerification otpVerification = new OtpVerification();
//        otpVerification.setUser(user);
//        otpVerification.setOtp(otp);
//        otpVerification.setUsed(false);
//        otpVerification.setType(otpType);
//        otpVerification.setCreatedBy("SYSTEM");
//        otpVerification.setExpiresAt(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)); // OTP valid for 5 minutes
//
//        return otpVerificationRepository.save(otpVerification);
//    }
//
//    public void sendOtp(Users user, String otp) {
//        twilioService.sendSms(user.getIsdCode(), user.getPhoneNumber(), otp);
//    }
//
//    public void trackOtpGeneration(Users user, OtpVerification otpVerification) {
//        SmsTracking smsTracking = new SmsTracking();
//        smsTracking.setUser(user);
//        smsTracking.setOtpVerification(otpVerification);
//        smsTracking.setCreatedBy("USER");
//        smsTracking.setAttempts(1);
//        smsTracking.setLastAttemptAt(LocalDateTime.now());
//
//        smsTrackingRepository.save(smsTracking);
//    }
//
//
//    public boolean validateOtp(Users user, String otp, Medium medium, HttpServletResponse response) {
//        if (medium == Medium.MOBILE) {
//            user = userRepository.findByPhoneNumber(user.getPhoneNumber())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        } else {
//            user = userRepository.findByEmail(user.getEmail())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        }
//
//        OtpVerification otpVerification = otpVerificationRepository.findByUserAndOtp(user, otp)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP."));
//
//        if (otpVerification.isUsed() || otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new IllegalArgumentException("OTP is either used or expired.");
//        }
//
//        otpVerification.setUsed(true);
//        otpVerification.setStatus(Status.VERIFIED);
//        otpVerificationRepository.save(otpVerification);
//
//        String jwtToken = jwtService.generateToken(user.getUsername());
//        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
//
//        return true;
//    }
//
//}
//package com.auth.AuthImpl.registraion.service;
//
//import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
//import com.auth.AuthImpl.registraion.entity.OtpVerification;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.enums.Medium;
//import com.auth.AuthImpl.registraion.enums.OtpType;
//import com.auth.AuthImpl.registraion.enums.Status;
//import com.auth.AuthImpl.registraion.repo.OtpVerificationRepository;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import com.auth.AuthImpl.utils.JWTService;
//import com.auth.AuthImpl.utils.config.TwilioService;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.http.HttpHeaders;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//
//@Service
//public class OtpService {
//
//    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
//
//    @Autowired
//    private OtpVerificationRepository otpVerificationRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TwilioService twilioService;
//
//    @Autowired
//    private JWTService jwtService;
//
//    private static final int OTP_EXPIRATION_MINUTES = 5;
//
//    public String generateOtp() {
//        return String.valueOf((int) (Math.random() * 900000) + 100000); // Example: random 6-digit OTP
//    }
//
//    public OtpVerification createAndSaveOtpVerification(Users user, String otp, OtpType otpType) {
//        validateUser(user); // Validate user existence
//
//        OtpVerification otpVerification = new OtpVerification();
//        otpVerification.setUser(user);
//        otpVerification.setOtp(otp);
//        otpVerification.setUsed(false);
//        otpVerification.setType(otpType);
//        otpVerification.setCreatedBy("SYSTEM");
//        otpVerification.setExpiresAt(LocalDateTime.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES));
//
//        return otpVerificationRepository.save(otpVerification);
//    }
//
//    public void sendOtp(Users user, OtpVerification otpVerification) {
//        String otp = otpVerification.getOtp();
//        twilioService.sendSms(user, otpVerification, user.getIsdCode(), user.getPhoneNumber(), otp);
//    }
//
//    public boolean validateOtp(Users user, String otp, Medium medium, HttpServletResponse response) {
//        user = findUserByMedium(user, medium);
//        OtpVerification otpVerification = otpVerificationRepository.findByUserAndOtp(user, otp)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP."));
//
//        verifyOtpValidity(otpVerification);
//
//        // Increment attempts
//        otpVerification.setAttempts(otpVerification.getAttempts() + 1);
//        otpVerificationRepository.save(otpVerification); // Save attempts
//
//        // Mark OTP as used
//        otpVerification.setUsed(true);
//        otpVerification.setStatus(Status.VERIFIED);
//        otpVerificationRepository.save(otpVerification);
//
//        String jwtToken = jwtService.generateToken(user.getUsername());
//        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
//
//        return true;
//    }
//
//
//    private Users findUserByMedium(Users user, Medium medium) {
//        if (medium == Medium.MOBILE) {
//            return userRepository.findByPhoneNumber(user.getPhoneNumber())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        } else {
//            return userRepository.findByEmail(user.getEmail())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        }
//    }
//
//    private void verifyOtpValidity(OtpVerification otpVerification) {
//        if (otpVerification.isUsed()) {
//            throw new IllegalArgumentException("OTP has already been used.");
//        }
//
//        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new IllegalArgumentException("OTP has expired.");
//        }
//
//        if (otpVerification.getAttempts() >= 5) { // Max attempts limit
//            throw new IllegalArgumentException("Maximum OTP attempts exceeded.");
//        }
//    }
//
//    private void validateUser(Users user) {
//        if (user == null || user.getPhoneNumber() == null && user.getEmail() == null) {
//            throw new IllegalArgumentException("User must have at least a phone number or an email.");
//        }
//    }
//
//    public void resendOtp(UserRequestDto userRequestDto, Medium medium) {
//        // Find the user based on the medium (email or phone)
//        Users user = findUserByMedium(userRequestDto, medium); // Convert UserRequestDto to Users
//
//        // Fetch the active OTP verification record for the user
//        OtpVerification otpVerification = otpVerificationRepository.findByUserAndIsUsedFalse(user)
//                .orElseThrow(() -> new IllegalArgumentException("No active OTP found for user."));
//
//        // Calculate wait time based on attempts
//        long waitTime = calculateWaitTime(otpVerification.getAttempts());
//
//        if (waitTime > 0) {
//            LocalDateTime nextSendTime = otpVerification.getCreatedAt().plusMinutes(waitTime);
//            if (LocalDateTime.now().isBefore(nextSendTime)) {
//                throw new IllegalArgumentException("You must wait before resending the OTP.");
//            }
//        }
//
//        String newOtp = generateOtp();
//        otpVerification.setOtp(newOtp);
//        otpVerification.setExpiresAt(LocalDateTime.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES));
//        otpVerification.setAttempts(otpVerification.getAttempts() + 1); // Increment attempts
//        otpVerificationRepository.save(otpVerification); // Save changes
//
//        sendOtp(user, otpVerification); // Send the new OTP
//    }
//
//
//
//    private Users findUserByMedium(UserRequestDto userRequestDto, Medium medium) {
//        if (medium == Medium.MOBILE) {
//            return userRepository.findByPhoneNumber(userRequestDto.getPhoneNumber())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        } else if (medium == Medium.EMAIL) {
//            return userRepository.findByEmail(userRequestDto.getEmail())
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        }
//        throw new IllegalArgumentException("Invalid medium provided.");
//    }
//
//
//    private long calculateWaitTime(int attempts) {
//        switch (attempts) {
//            case 0:
//                return 1;  // 1 minute
//            case 1:
//                return 2;  // 2 minutes
//            case 2:
//                return 5;  // 5 minutes
//            case 3:
//                return 10; // 10 minutes
//            case 4:
//                return 60; // 60 minutes
//            default:
//                throw new IllegalArgumentException("Maximum resend attempts reached.");
//        }
//    }
//}
//
//
    package com.auth.AuthImpl.registraion.service;

    import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
    import com.auth.AuthImpl.registraion.entity.OtpVerification;
    import com.auth.AuthImpl.registraion.entity.Users;
    import com.auth.AuthImpl.registraion.enums.Medium;
    import com.auth.AuthImpl.registraion.enums.OtpType;
    import com.auth.AuthImpl.registraion.enums.Status;
    import com.auth.AuthImpl.registraion.repo.OtpVerificationRepository;
    import com.auth.AuthImpl.registraion.repo.UserRepository;
    import com.auth.AuthImpl.utils.JWTService;
    import com.auth.AuthImpl.utils.config.TwilioService;
    import com.auth.AuthImpl.utils.validation.Validation;
    import jakarta.servlet.http.HttpServletResponse;
    import org.apache.http.HttpHeaders;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.time.temporal.ChronoUnit;
    import java.util.List;

    @Service
    public class OtpService {

        private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
        private static final int OTP_EXPIRATION_MINUTES = 5;
        private static final int MAX_ATTEMPTS = 5;

        @Autowired
        private OtpVerificationRepository otpVerificationRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TwilioService twilioService;

        @Autowired
        private JWTService jwtService;

        @Autowired
        private Validation otpValidationService;

        public String generateOtp() {
            return String.valueOf((int) (Math.random() * 900000) + 100000); // Example: random 6-digit OTP
        }

        public OtpVerification createAndSaveOtpVerification(Users user, String otp, OtpType otpType) {
            otpValidationService.validateUser(user); // Validate user

            OtpVerification otpVerification = new OtpVerification();
            otpVerification.setUser(user);
            otpVerification.setOtp(otp);
            otpVerification.setUsed(false);
            otpVerification.setType(otpType);
            otpVerification.setCreatedBy("SYSTEM");
            otpVerification.setExpiresAt(LocalDateTime.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES));
            otpVerification.setAttempts(0); // Initialize attempts to 0

            return otpVerificationRepository.save(otpVerification);
        }

        public void sendOtp(Users user, OtpVerification otpVerification) {
            otpValidationService.validateUser(user);
            otpValidationService.verifyOtpValidity(otpVerification);

                twilioService.sendSms(user, otpVerification, user.getIsdCode(), user.getPhoneNumber(), otpVerification.getOtp());
        }

        public boolean validateOtp(Users user, String otp, Medium medium, HttpServletResponse response) {
            user = otpValidationService.findUserByMedium(user, medium); // Use validation service to find user

            OtpVerification otpVerification = otpVerificationRepository.findByUserAndOtp(user, otp)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid OTP."));

            otpValidationService.verifyOtpValidity(otpVerification); // Validate OTP

            otpVerification.setAttempts(otpVerification.getAttempts() + 1);
            otpVerification.setUsed(true);
            otpVerification.setStatus(Status.VERIFIED);
            otpVerificationRepository.save(otpVerification); // Save changes

            String jwtToken = jwtService.generateToken(user.getUsername());
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);

            return true;
        }


        public void resendOtp(UserRequestDto userRequestDto, Medium medium) {
            Users user = findUserByMedium(userRequestDto, medium);

            List<OtpVerification> otpVerifications = otpVerificationRepository.findByUserAndIsUsedFalse(user);

            if (otpVerifications.isEmpty()) {
                throw new IllegalArgumentException("No active OTP found for user.");
            }

            OtpVerification activeOtp = otpVerifications.stream()
                    .filter(otp -> otp.getExpiresAt().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No valid OTP found for user."));

            otpValidationService.validateResendOtp(activeOtp);

            String newOtp = generateOtp();
            activeOtp.setOtp(newOtp);
            activeOtp.setExpiresAt(LocalDateTime.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES));
            activeOtp.setAttempts(activeOtp.getAttempts() + 1); // Increment attempts
            otpVerificationRepository.save(activeOtp);

            sendOtp(user, activeOtp);
        }

        private Users findUserByMedium(UserRequestDto userRequestDto, Medium medium) {
            if (medium == Medium.MOBILE) {
                return userRepository.findByPhoneNumber(userRequestDto.getPhoneNumber())
                        .orElseThrow(() -> new IllegalArgumentException("User not found for phone number: " + userRequestDto.getPhoneNumber()));
            } else if (medium == Medium.EMAIL) {
                return userRepository.findByEmail(userRequestDto.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + userRequestDto.getEmail()));
            }
            throw new IllegalArgumentException("Invalid medium provided.");
        }


//        private Users findUserByMedium(Users user, Medium medium) {
//            if (medium == Medium.MOBILE) {
//                return userRepository.findByPhoneNumber(user.getPhoneNumber())
//                        .orElseThrow(() -> new IllegalArgumentException("User not found for phone number: " + user.getPhoneNumber()));
//            } else if (medium == Medium.EMAIL) {
//                return userRepository.findByEmail(user.getEmail())
//                        .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + user.getEmail()));
//            }
//            throw new IllegalArgumentException("Invalid medium provided.");
//        }
//
//        private void verifyOtpValidity(OtpVerification otpVerification) {
//            if (otpVerification.isUsed()) {
//                throw new IllegalArgumentException("OTP has already been used.");
//            }
//
//            if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
//                throw new IllegalArgumentException("OTP has expired.");
//            }
//
//            if (otpVerification.getAttempts() >= MAX_ATTEMPTS) {
//                throw new IllegalArgumentException("Maximum OTP attempts exceeded.");
//            }
//        }
//
//        private void validateUser(Users user) {
//            if (user == null || (user.getPhoneNumber() == null && user.getEmail() == null)) {
//                throw new IllegalArgumentException("User must have at least a phone number or an email.");
//            }
//        }
//
//        private long calculateWaitTime(int attempts) {
//            switch (attempts) {
//                case 0: return 1;  // 1 minute
//                case 1: return 2;  // 2 minutes
//                case 2: return 5;  // 5 minutes
//                case 3: return 10; // 10 minutes
//                case 4: return 60; // 60 minutes
//                default: throw new IllegalArgumentException("Maximum resend attempts reached.");
//            }
//        }
    }
