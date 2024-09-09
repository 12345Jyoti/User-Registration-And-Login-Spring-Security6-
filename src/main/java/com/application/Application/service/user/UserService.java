package com.application.Application.service.user;


import com.application.Application.dto.UsersDTO;
import com.application.Application.entity.Users;

import java.util.List;

public interface UserService {
    UsersDTO registerUser(UsersDTO usersDTO) throws Exception ;

    boolean verifyEmailOtp(String email, String otp);

    boolean verifyPhoneOtp(String phoneNumber, String otp);

    void sendLoginOtp(String identifier) throws Exception; // Sends OTP based on email or phone

    String loginWithOtp(String identifier, String otp) throws Exception;

    String verify(Users user);

    List<UsersDTO> getAllUsers();
}

