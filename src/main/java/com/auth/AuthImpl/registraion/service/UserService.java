package com.auth.AuthImpl.registraion.service;


import com.auth.AuthImpl.registraion.dto.UsersResponseDTO;
import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.auth.AuthImpl.registraion.enums.OtpType;
import com.auth.AuthImpl.registraion.mapper.UsersMapper;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
//public class UserService {
//
//    @Autowired
//    private RegistrationServiceLocator registrationServiceLocator;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public Users registerOrLoginUser(UserRequestDto userRequestDto) {
//        String phoneNumber = userRequestDto.getPhoneNumber();
//        String email = userRequestDto.getEmail();
//        Medium medium;
//
//        if (phoneNumber != null && !phoneNumber.isEmpty()) {
//            medium = Medium.MOBILE;
//        } else if (email != null && !email.isEmpty()) {
//            medium = Medium.EMAIL;
//        } else {
//            throw new IllegalArgumentException("Either phone number or email must be provided.");
//        }
//
//        Optional<Users> existingUser = userRepository.findByPhoneNumberOrEmail(phoneNumber, email);
//
//        RegistrationInterface registrationService = registrationServiceLocator.getRegistrationService(medium);
//
//        if (existingUser.isPresent()) {
//            return registrationService.handleLogin(existingUser.get());
//        } else {
//            return registrationService.handleSignUp(userRequestDto);
//        }
//    }
//}
//
//package com.auth.AuthImpl.registraion.service;

import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private RegistrationServiceLocator registrationServiceLocator;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private OtpService otpService;
//
//    public Object registerOrLoginUser(UserRequestDto userRequestDto, HttpServletResponse response) {
//        Medium medium = determineRegistrationMedium(userRequestDto);
//        Optional<Users> existingUser = userRepository.findByPhoneNumberOrEmail(userRequestDto.getPhoneNumber(), userRequestDto.getEmail());
//        RegistrationInterface registrationService = registrationServiceLocator.getRegistrationService(medium);
//
//        if (existingUser.isPresent()) {
//            return handleExistingUser(existingUser.get(), userRequestDto.getOtp(), medium, registrationService, response);
//        } else {
//            Users newUser = registrationService.handleSignUp(userRequestDto);
//            return newUser;
//        }
//    }
//
//    private Medium determineRegistrationMedium(UserRequestDto userRequestDto) {
//
//
//        if (userRequestDto.getPhoneNumber() != null && !userRequestDto.getPhoneNumber().isEmpty()) {
//            return Medium.MOBILE;
//        } else if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty()) {
//            return Medium.EMAIL;
//        } else {
//            throw new IllegalArgumentException("Either phone number or email must be provided.");
//        }
//    }
////remove object
//    private Object handleExistingUser(Users user, String otp, Medium medium, RegistrationInterface registrationService, HttpServletResponse response) {
//        if (otp != null && !otp.isEmpty()) {
//            boolean otpVerified = otpService.validateOtp(user, otp, medium, response); // Verify OTP
//            if (otpVerified) {
//                return "Login successful. Welcome back!";
//            } else {
//                throw new IllegalArgumentException("OTP verification failed.");
//            }
//        } else {
//            registrationService.handleLogin(user);
//            return "OTP sent to your registered mobile number/email for login.";
//        }
//    }
//}

@Service
public class UserService {

    @Autowired
    private RegistrationServiceLocator registrationServiceLocator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    public UsersResponseDTO registerOrLoginUser(UserRequestDto userRequestDto, HttpServletResponse response) {
        Medium medium = determineRegistrationMedium(userRequestDto);
        Optional<Users> existingUser = findExistingUser(userRequestDto);

        RegistrationInterface registrationService = registrationServiceLocator.getRegistrationService(medium);

        if (existingUser.isPresent()) {
            return handleExistingUser(existingUser.get(), userRequestDto.getOtp(), medium, registrationService, response);
        } else {
            Users newUser = registrationService.handleSignUp(userRequestDto);
            return new UsersResponseDTO(newUser, "User registered successfully, OTP sent!");
        }
    }

    private Optional<Users> findExistingUser(UserRequestDto userRequestDto) {
        return userRepository.findByPhoneNumber(userRequestDto.getPhoneNumber())
                .or(() -> userRepository.findByEmail(userRequestDto.getEmail()));
    }



    private Medium determineRegistrationMedium(UserRequestDto userRequestDto) {
        if (userRequestDto.getPhoneNumber() != null && !userRequestDto.getPhoneNumber().isEmpty()) {
            return Medium.MOBILE;
        } else if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty()) {
            return Medium.EMAIL;
        } else {
            throw new IllegalArgumentException("Either phone number or email must be provided.");
        }
    }

    private UsersResponseDTO handleExistingUser(Users user, String otp, Medium medium, RegistrationInterface registrationService, HttpServletResponse response) {
        if (otp != null && !otp.isEmpty()) {
            boolean otpVerified = otpService.validateOtp(user, otp, medium, response); // Verify OTP
            if (otpVerified) {
                return UsersMapper.mapToDTO(user, "Login successful. Welcome back!");
            } else {
                throw new IllegalArgumentException("OTP verification failed.");
            }
        } else {
            registrationService.handleLogin(user);
            return UsersMapper.mapToDTO(user, "OTP sent to your registered mobile number/email for login.");
        }
    }

    // Mapping Users entity to UsersResponseDTO

}
