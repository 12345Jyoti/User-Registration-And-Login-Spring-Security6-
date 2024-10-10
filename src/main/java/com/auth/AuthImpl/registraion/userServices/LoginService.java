////package com.auth.AuthImpl.registraion.userServices;
////
////import com.auth.AuthImpl.registraion.dtos.request.LoginRequestDto;
////import com.auth.AuthImpl.registraion.dtos.request.OtpVerificationRequestDto;
////import com.auth.AuthImpl.registraion.dtos.response.OtpVerificationResponseDto;
////import com.auth.AuthImpl.registraion.entity.OtpTracking;
////import com.auth.AuthImpl.registraion.entity.OtpVerification;
////import com.auth.AuthImpl.registraion.entity.Users;
////import com.auth.AuthImpl.registraion.enums.OtpType;
////import com.auth.AuthImpl.registraion.repo.OtpTrackingRepository;
////import com.auth.AuthImpl.registraion.repo.OtpVerificationRepository;
////import com.auth.AuthImpl.registraion.repo.UserRepository;
////import com.auth.AuthImpl.utils.JWTService;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.stereotype.Service;
////
////import java.time.LocalDateTime;
////import java.time.temporal.ChronoUnit;
////import java.util.Collections;
////import java.util.Random;
////
////
////@Service
////public class LoginService {
////
////    @Autowired
////    private OtpVerificationRepository otpVerificationRepository;
////
////    @Autowired
////    private OtpTrackingRepository otpTrackingRepository;
////
////    @Autowired
////    private UserRepository userRepository;
////
////    @Autowired
////    private AuthenticationManager authenticationManager;
////
////    @Autowired
////    private JWTService jwtService;
//// // Method to generate and send OTP
////        public void generateAndSendOtp(LoginRequestDto requestDto) {
////            if (requestDto.getMobile() != null) {
////                String isdCode = requestDto.getMobile().getIsdCode();
////                String phoneNumber = requestDto.getMobile().getPhoneNumber();
////
////                Users user = userRepository.findByIsdCodeInAndPhoneNumber(Collections.singletonList(isdCode), phoneNumber)
////                        .orElseThrow(() -> new RuntimeException("User not found"));
////
////                String generatedOtp = generateOtp();
////                saveOtpVerificationAndTracking(user, generatedOtp, OtpType.MOBILE);
////            } else if (requestDto.getEmail() != null) {
////                Users user = userRepository.findByEmail(requestDto.getEmail().getEmailAddress())
////                        .orElseThrow(() -> new RuntimeException("User not found"));
////
////                String generatedOtp = generateOtp();
////                saveOtpVerificationAndTracking(user, generatedOtp, OtpType.EMAIL);
////            }
////        }
////
////        // Common method to save OTP verification and tracking
////        private void saveOtpVerificationAndTracking(Users user, String generatedOtp, OtpType otpType) {
////            // Create OTP Verification Entry
////            OtpVerification otpVerification = new OtpVerification();
////            otpVerification.setUser(user); // Ensure user is not null
////            otpVerification.setOtp(generatedOtp);
////            otpVerification.setUsed(false);
////            otpVerification.setCreatedBy("SYSTEM");
////            otpVerification.setExpiresAt(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
////            otpVerification.setType(otpType);
////
////            // Save OTP Verification entry
////            otpVerificationRepository.save(otpVerification);
////            // Create OTP Tracking Entry
////            OtpTracking otpTracking = new OtpTracking();
////            otpTracking.setUser(user);
////            otpTracking.setOtpVerification(otpVerification);
////            otpTracking.setAttempts(0);
////            otpTracking.setCreatedBy("SYSTEM");
////            otpTracking.setLastAttemptAt(LocalDateTime.now());
////
////            // Save OTP Tracking entry
////            otpTrackingRepository.save(otpTracking);
////
////            // TODO: Send OTP to the user via SMS or Email
////            System.out.println("Generated OTP for user: " + user.getUsername() + " is: " + generatedOtp);
////        }
////
////        // Method to verify OTP
////        public OtpVerificationResponseDto verifyOtp(OtpVerificationRequestDto requestDto) {
////            // Debugging output
////            System.out.println("Verifying OTP for user: " + requestDto.getUserName());
////            System.out.println("OTP: " + requestDto.getOtp());
////            System.out.println("OTP Type: " + requestDto.getOtpType());
////
////            // Retrieve the OTP verification record
////            OtpVerification otpVerification = otpVerificationRepository.findByUser_UsernameAndOtpAndType(
////                    requestDto.getUserName(), requestDto.getOtp(), requestDto.getOtpType());
////
////            OtpVerificationResponseDto responseDto = new OtpVerificationResponseDto();
////
////            // Check if the OTP verification record was found
////            if (otpVerification == null) {
////                responseDto.setSuccess(false);
////                responseDto.setMessage("No OTP record found for the given username or user is missing.");
////                return responseDto; // Early return for clarity
////            }
////
////            // At this point, we have an otpVerification object
////            System.out.println("OTP Verification found: " + otpVerification.toString());
////
////            // Verify if the OTP is valid and not expired
////            if (!otpVerification.isUsed() && !otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
////                // Mark OTP as used
////                otpVerification.setUsed(true);
////                otpVerificationRepository.save(otpVerification); // Save the updated status
////
////                // Generate JWT token
////                String jwtToken = jwtService.generateToken(requestDto.getUserName());
////
////                // Create successful response
////                responseDto.setSuccess(true);
////                responseDto.setMessage("OTP verified successfully.");
////                responseDto.setUserName(requestDto.getUserName());
////                responseDto.setJwtToken(jwtToken);
////            } else {
////                responseDto.setSuccess(false);
////                responseDto.setMessage("Invalid or expired OTP.");
////            }
////
////            return responseDto;
////        }
////
////
////
////
////    // Helper method to generate a 6-digit OTP
////        private String generateOtp() {
////            Random random = new Random();
////            StringBuilder otp = new StringBuilder();
////            for (int i = 0; i < 6; i++) {
////                otp.append(random.nextInt(10)); // Generates a random digit
////            }
////            return otp.toString();
////        }
////    }
//package com.auth.AuthImpl.registraion.userServices;
//
//import com.auth.AuthImpl.registraion.dtos.request.LoginRequestDto;
//import com.auth.AuthImpl.registraion.dtos.request.OtpVerificationRequestDto;
//import com.auth.AuthImpl.registraion.dtos.response.OtpVerificationResponseDto;
//import com.auth.AuthImpl.registraion.entity.OtpTracking;
//import com.auth.AuthImpl.registraion.entity.OtpVerification;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.enums.OtpType;
//import com.auth.AuthImpl.registraion.repo.OtpTrackingRepository;
//import com.auth.AuthImpl.registraion.repo.OtpVerificationRepository;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import com.auth.AuthImpl.utils.JWTService;
//import com.auth.AuthImpl.utils.config.TwilioService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Collections;
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//public class LoginService {
//
//    @Autowired
//    private OtpVerificationRepository otpVerificationRepository;
//
//    @Autowired
//    private OtpTrackingRepository otpTrackingRepository;
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
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    /**
//     * Generates OTP and sends it via SMS or Email based on the user input.
//     *
//     * @param requestDto Contains user login data like mobile number or email.
//     */
//    public void generateAndSendOtp(LoginRequestDto requestDto) {    //todo: with medium
//        try {
//            if (requestDto.getMobile() != null) {
//                // Generate OTP for mobile number
//                Users user = findUserByMobile(requestDto.getMobile().getIsdCode(), requestDto.getMobile().getPhoneNumber());
//                String generatedOtp = generateOtp();
//                saveOtpVerificationAndTracking(user, generatedOtp, OtpType.MOBILE);
//
//                // Send OTP via SMS using Twilio
//                String toPhoneNumber = requestDto.getMobile().getIsdCode() + requestDto.getMobile().getPhoneNumber();
//                try{
//                    twilioService.sendSms(toPhoneNumber, generatedOtp);
//
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//                //todo: also OptTrack here
//
//            } else if (requestDto.getEmail() != null) {
//                // Generate OTP for email
//                Users user = findUserByEmail(requestDto.getEmail().getEmailAddress());
//                String generatedOtp = generateOtp();
//                saveOtpVerificationAndTracking(user, generatedOtp, OtpType.EMAIL);
//
//                // TODO: Send OTP via Email (implement email service if needed)
//            } else {
//                throw new IllegalArgumentException("Either mobile or email must be provided.");
//            }
//        } catch (Exception e) {
//            // Log the error and handle any exception during OTP generation
//            System.err.println("Error during OTP generation: " + e.getMessage());
//            throw new IllegalArgumentException("Failed to generate and send OTP.", e);
//        }
//    }
//
//    /**
//     * Method to verify the OTP entered by the user.
//     *
//     * @param requestDto Contains user name, OTP, and OTP type.
//     * @return A response object containing the verification result and JWT token (if successful).
//     */
//    public OtpVerificationResponseDto verifyOtp(OtpVerificationRequestDto requestDto) {
//        OtpVerificationResponseDto responseDto = new OtpVerificationResponseDto();
//
//        try {
//            // Retrieve the OTP verification record
//            OtpVerification otpVerification = otpVerificationRepository.findByUser_UsernameAndOtpAndType(
//                    requestDto.getUserName(), requestDto.getOtp(), requestDto.getOtpType());
//
//            // Handle the case when no OTP record is found
//            if (otpVerification == null) {
//                responseDto.setSuccess(false);
//                responseDto.setMessage("No OTP record found for the given username.");
//                return responseDto;
//            }
//
//            // Validate OTP (check if it's used or expired)
//            if (!otpVerification.isUsed() && !otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
//                // Mark OTP as used
//                otpVerification.setUsed(true);
//                otpVerificationRepository.save(otpVerification);
//
//                // Generate JWT token
//                String jwtToken = jwtService.generateToken(requestDto.getUserName());
//
//                // Successful verification
//                responseDto.setSuccess(true);
//                responseDto.setMessage("OTP verified successfully.");
//                responseDto.setUserName(requestDto.getUserName());
//                responseDto.setJwtToken(jwtToken);
//            } else {
//                // OTP is either used or expired
//                responseDto.setSuccess(false);
//                responseDto.setMessage("Invalid or expired OTP.");
//            }
//        } catch (Exception e) {
//            // Log and handle the error during OTP verification
//            System.err.println("Error during OTP verification: " + e.getMessage());
//            responseDto.setSuccess(false);
//            responseDto.setMessage("OTP verification failed due to a system error.");
//        }
//
//        return responseDto;
//    }
//
//    /**
//     * Helper method to find a user by their mobile number.
//     *
//     * @param isdCode     ISD code for the country.
//     * @param phoneNumber Mobile number of the user.
//     * @return User object.
//     */
//    private Users findUserByMobile(String isdCode, String phoneNumber) {
//        return userRepository.findByIsdCodeInAndPhoneNumber(Collections.singletonList(isdCode), phoneNumber)
//                .orElseThrow(() -> new RuntimeException("User not found with mobile: " + phoneNumber));
//    }
//
//    /**
//     * Helper method to find a user by their email address.
//     *
//     * @param emailAddress Email address of the user.
//     * @return User object.
//     */
//    private Users findUserByEmail(String emailAddress) {
//        return userRepository.findByEmail(emailAddress)
//                .orElseThrow(() -> new RuntimeException("User not found with email: " + emailAddress));
//    }
//
//    /**
//     * Saves the OTP verification details and tracking information for the user.
//     *
//     * @param user         User object.
//     * @param generatedOtp Generated OTP value.
//     * @param otpType      Type of OTP (email or mobile).
//     */
//    private void saveOtpVerificationAndTracking(Users user, String generatedOtp, OtpType otpType) {
//        try {
//            // Save OTP Verification
//            OtpVerification otpVerification = new OtpVerification();
//            otpVerification.setUser(user);
//            otpVerification.setOtp(generatedOtp);
//            otpVerification.setUsed(false);
//            otpVerification.setCreatedBy("SYSTEM");
//            otpVerification.setExpiresAt(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)); // OTP valid for 5 minutes
//            otpVerification.setType(otpType);
//
//            otpVerificationRepository.save(otpVerification);
//
//            // Save OTP Tracking
//            OtpTracking otpTracking = new OtpTracking();
//            otpTracking.setUser(user);
//            otpTracking.setOtpVerification(otpVerification);
//            otpTracking.setAttempts(0);
//            otpTracking.setCreatedBy("SYSTEM");
//            otpTracking.setLastAttemptAt(LocalDateTime.now());
//
//            otpTrackingRepository.save(otpTracking);
//
//            // TODO: Integrate with SMS/Email service to send OTP
//            System.out.println("Generated OTP for user: " + user.getUsername() + " is: " + generatedOtp);
//        } catch (Exception e) {
//            System.err.println("Error during OTP saving: " + e.getMessage());
//            throw new IllegalArgumentException("Failed to save OTP verification details.", e);
//        }
//    }
//
//    /**
//     * Helper method to generate a random 6-digit OTP.
//     *
//     * @return A 6-digit OTP string.
//     */
//    private String generateOtp() {
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//        for (int i = 0; i < 6; i++) {
//            otp.append(random.nextInt(10)); // Generates a random digit between 0-9
//        }
//        return otp.toString();
//    }
//
//
//
//    public boolean validateUserCredentials(String username, String password) {
//        // Find the user by username
//        Users user = userRepository.findByUsername(username);
//        if (user != null) {
//            // Check if the provided password matches the stored hashed password
//            return passwordEncoder.matches(password, user.getPassword());
//        }
//        // Return false if the user does not exist or the password doesn't match
//        return false;
//    }
//}
