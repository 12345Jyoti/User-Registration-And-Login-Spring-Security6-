package com.auth.AuthImpl.registraion.service;

import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.OtpType;
import com.auth.AuthImpl.registraion.mapper.UsersMapper;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailRegistrationService implements RegistrationInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

//    @Autowired
//    private EmailService emailService;

    @Override
    public Users handleLogin(Users existingUser) {
        String generatedOtp = otpService.generateOtp();
        otpService.createAndSaveOtpVerification(existingUser, generatedOtp, OtpType.EMAIL);
//        emailService.sendEmail(existingUser.getEmail(), generatedOtp);
        return existingUser;
    }

    @Override
    public Users handleSignUp(UserRequestDto userRequestDto) {
        Users newUser = UsersMapper.dtoToEntity(userRequestDto);
        Users savedUser = userRepository.save(newUser);

        String generatedOtp = otpService.generateOtp();
        otpService.createAndSaveOtpVerification(savedUser, generatedOtp, OtpType.EMAIL);
//        emailService.sendEmail(savedUser.getEmail(), generatedOtp);

        return savedUser;
    }
}
