package com.application.Application.registraion.userServices;

import com.application.Application.registraion.dto.UsersRequestDTO;
import com.application.Application.registraion.dto.UsersResponseDTO;
import com.application.Application.registraion.entity.Users;

import java.util.List;

public interface UserService {

    UsersResponseDTO registerUser(UsersRequestDTO usersRequestDTO) throws Exception;

    boolean verifyEmailOtp(String email, String otp);

    boolean verifyPhoneOtp(String phoneNumber, String otp);

    void sendLoginOtp(String identifier) throws Exception;

    String loginWithOtp(String identifier, String otp) throws Exception;

    String verify(Users user);

    List<UsersResponseDTO> getAllUsers();
}
