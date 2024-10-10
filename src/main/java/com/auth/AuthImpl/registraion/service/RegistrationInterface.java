package com.auth.AuthImpl.registraion.service;

import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.Users;


public interface RegistrationInterface {
    Users handleLogin(Users existingUser);

    Users handleSignUp(UserRequestDto userRequestDto);
}
