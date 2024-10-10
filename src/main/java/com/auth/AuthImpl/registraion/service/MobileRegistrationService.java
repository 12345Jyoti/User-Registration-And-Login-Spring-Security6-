//package com.auth.AuthImpl.registraion.service;
//import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.enums.OtpType;
//import com.auth.AuthImpl.registraion.mapper.UsersMapper;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import com.auth.AuthImpl.registraion.entity.OtpVerification;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class MobileRegistrationService implements RegistrationInterface {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private OtpService otpService;
//
//    @Override
//    public Users register(UserRequestDto userRequestDto) {
//        String phoneNumber = userRequestDto.getPhoneNumber();
//        String email = userRequestDto.getEmail();
//        Optional<Users> existingUser = userRepository.findByPhoneNumberOrEmail(phoneNumber, email);
//
//        if (existingUser.isPresent()) {
//            return handleLogin(existingUser.get());
//        } else {
//            return handleSignUp(userRequestDto);
//        }
//    }
//
//    private Users handleLogin(Users existingUser) {
//        String generatedOtp = otpService.generateOtp();
//        OtpVerification otpVerification = otpService.saveOtpVerification(existingUser, generatedOtp, OtpType.MOBILE);
//        otpService.sendOtp(existingUser, generatedOtp);
//        otpService.trackOtpGeneration(existingUser, otpVerification);
//        return existingUser;
//    }
//
//    private Users handleSignUp(UserRequestDto userRequestDto) {
//        Users newUser = UsersMapper.dtoToEntity(userRequestDto);
//        Users savedUser = userRepository.save(newUser);
//        String generatedOtp = otpService.generateOtp();
//        OtpVerification otpVerification = otpService.saveOtpVerification(savedUser, generatedOtp, OtpType.MOBILE);
//        otpService.sendOtp(savedUser, generatedOtp);
//        otpService.trackOtpGeneration(savedUser, otpVerification);
//        return savedUser;
//    }
//}
package com.auth.AuthImpl.registraion.service;

import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.OtpVerification;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.OtpType;
import com.auth.AuthImpl.registraion.mapper.UsersMapper;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileRegistrationService implements RegistrationInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Override
    public Users handleLogin(Users existingUser) {
        String generatedOtp = otpService.generateOtp();
        OtpVerification otpVerification = otpService.createAndSaveOtpVerification(existingUser, generatedOtp, OtpType.MOBILE);
        otpService.sendOtp(existingUser, otpVerification);
        return existingUser;
    }

    @Override
    public Users handleSignUp(UserRequestDto userRequestDto) {
        Users newUser = UsersMapper.dtoToEntity(userRequestDto);
        newUser.setPassword("Password");
        newUser.setCreatedBy("USER");
        Users savedUser = userRepository.save(newUser);
        String generatedOtp = otpService.generateOtp();
        OtpVerification otpVerification = otpService.createAndSaveOtpVerification(savedUser, generatedOtp, OtpType.MOBILE);
        otpService.sendOtp(savedUser, otpVerification);
        return savedUser;
    }
}
