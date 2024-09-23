package com.auth.AuthImpl.registraion.userServices;

import com.auth.AuthImpl.registraion.dto.UsersRequestDTO;
import com.auth.AuthImpl.registraion.dto.UsersResponseDTO;
import com.auth.AuthImpl.registraion.entity.Users;

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
