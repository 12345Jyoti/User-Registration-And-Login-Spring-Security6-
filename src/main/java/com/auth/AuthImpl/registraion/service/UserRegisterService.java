//package com.auth.AuthImpl.registraion.service;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.registraion.dto.UserDto;
//import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.enums.Medium;
//import com.auth.AuthImpl.registraion.enums.OtpType;
//import com.auth.AuthImpl.registraion.enums.Status;
//import com.auth.AuthImpl.registraion.mapper.UsersMapper;
//import com.auth.AuthImpl.registraion.repo.OtpVerificationRepository;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import com.auth.AuthImpl.utils.JWTService;
//import com.auth.AuthImpl.utils.config.TwilioService;
//import com.auth.AuthImpl.registraion.entity.OtpVerification;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.http.HttpHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Optional;
//
//@Service
//public class UserRegisterService {   //use service locator
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private TwilioService twilioService;
//
//    @Autowired
//    private OtpVerificationRepository otpVerificationRepository;
//
//    @Autowired
//    private JWTService jwtService;
//
//    @Transactional
//    public Users registerOrLoginUser(UserRequestDto userRequestDto) {
//        if (userRequestDto.getMedium() == Medium.MOBILE) {
//            return handlePhoneRequest(userRequestDto);
//        } else if (userRequestDto.getMedium() == Medium.EMAIL) {
//            return handleEmailRequest(userRequestDto);
//        } else {
//            throw new IllegalArgumentException("Invalid medium provided.");
//        }
//    }
//
//    private Users handlePhoneRequest(UserRequestDto userRequestDto) {
//        String phoneNumber = userRequestDto.getPhoneNumber();
////        String isdCode = userRequestDto.getIsdCode();
//
//        Optional<Users> existingUser = userRepository.findByPhoneNumber(phoneNumber);
//
//        if (existingUser.isPresent()) {
//            return handleLogin(existingUser.get());
//        } else {
//            return handleSignUp(userRequestDto);
//        }
//    }
//
//    private Users handleEmailRequest(UserRequestDto userRequestDto) {
//        String email = userRequestDto.getEmail();
//
//
//        Optional<Users> existingUser = userRepository.findByEmail(email);
//
//        if (existingUser.isPresent()) {
//            return handleLogin(existingUser.get());
//        } else {
//            return handleSignUp(userRequestDto);
//        }
//    }
//
//    private Users handleLogin(Users existingUser) {
//        String generatedOtp = generateOtp();
//        saveOtpVerificationAndTracking(existingUser, generatedOtp);
//        twilioService.sendSms(existingUser.getIsdCode(), existingUser.getPhoneNumber(), generatedOtp);
//
//        return existingUser;
//    }
//
//    private Users handleSignUp(UserRequestDto userRequestDto) {
//        UserDto userDto = new UserDto();
//        if (userRequestDto.getMedium() == Medium.MOBILE) {
//            userDto.setUsername(userRequestDto.getPhoneNumber());
//            userDto.setPhoneNumber(userRequestDto.getPhoneNumber());
//            userDto.setIsdCode(userRequestDto.getIsdCode());
//        } else {
//            userDto.setUsername(userRequestDto.getEmail().split("@")[0]);
//            userDto.setEmail(userRequestDto.getEmail());
//        }
//
//        Users newUser = UsersMapper.dtoToEntity(userDto);
//        Users savedUser = userRepository.save(newUser);
//
//        String generatedOtp = generateOtp();
//        saveOtpVerificationAndTracking(savedUser, generatedOtp);
//        twilioService.sendSms(userDto.getIsdCode(),userDto.getPhoneNumber(), generatedOtp);
//
//        return savedUser;
//    }
//
//    private String generateOtp() {
//        // Implement OTP generation logic (could be a random 6-digit number)
//        return String.valueOf((int)(Math.random() * 900000) + 100000); // Example: random 6-digit OTP
//    }
//
//    private void saveOtpVerificationAndTracking(Users user, String generatedOtp) {
//        OtpVerification otpVerification = new OtpVerification();
//        otpVerification.setUser(user);
//        otpVerification.setOtp(generatedOtp);
//        otpVerification.setUsed(false);
//        otpVerification.setType(OtpType.MOBILE);
//        otpVerification.setCreatedBy("SYSTEM");
//        otpVerification.setExpiresAt(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)); // OTP valid for 5 minutes
//
//        otpVerificationRepository.save(otpVerification);
//    }
//
//    public boolean verifyOtp(String userIdentifier, String otp, Medium medium, HttpServletResponse response) {
//        Users user;
//        if (medium == Medium.MOBILE) {
//            user = userRepository.findByPhoneNumber(userIdentifier)
//                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
//        } else {
//            user = userRepository.findByEmail(userIdentifier)
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
//
//        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
//
//        return true; // OTP verified successfully
//    }
//}
//
