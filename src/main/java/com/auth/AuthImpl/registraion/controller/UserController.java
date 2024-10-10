package com.auth.AuthImpl.registraion.controller;

import com.auth.AuthImpl.registraion.dto.UsersResponseDTO;
import com.auth.AuthImpl.registraion.dtos.DefaultError;
import com.auth.AuthImpl.registraion.dtos.FormTypeRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.*;
import com.auth.AuthImpl.registraion.enums.FormType;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.auth.AuthImpl.registraion.form.RegisterForm;
import com.auth.AuthImpl.registraion.form.FormResponseService;
import com.auth.AuthImpl.registraion.form.LoginService;
import com.auth.AuthImpl.registraion.mapper.UsersMapper;
import com.auth.AuthImpl.registraion.service.OtpService;
import com.auth.AuthImpl.registraion.service.RegistrationServiceLocator;
import com.auth.AuthImpl.registraion.service.UserService;
import com.auth.AuthImpl.utils.ApiResponse;
import com.auth.AuthImpl.utils.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    RegistrationServiceLocator registrationServiceLocator;
    @Autowired
    OtpService otpService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private FormResponseService formResponseService;

    @Autowired
    private JWTService jwtService;


    @GetMapping("/getForm")
    public ApiResponse<RegisterForm, DefaultError> getRegistrationForm(@Valid @RequestBody FormTypeRequestDto formTypeDto) {
        FormType formType = formTypeDto.getFormType();
        RegisterForm formResponse = formResponseService.getRegistrationForm(formType);
        return ApiResponse.success(formResponse, "Form retrieved successfully.", null);
    }

    @PostMapping("/register")
    public ApiResponse<UsersResponseDTO, DefaultError> registerOrLogin(
            @Valid @RequestBody UserRequestDto userRequestDto,
            HttpServletResponse response) {
        UsersResponseDTO responseDto = userService.registerOrLoginUser(userRequestDto, response);
        return ApiResponse.success(responseDto, responseDto.getMessage(), null);
    }

    @PostMapping("/resend")
    public ApiResponse<UsersResponseDTO, DefaultError> resendOtp(@Valid @RequestBody UserRequestDto userRequestDto) {
        Medium medium = userRequestDto.getMedium();
        otpService.resendOtp(userRequestDto, medium);
        UsersResponseDTO responseDto = UsersMapper.mapToUserRequestDto(userRequestDto);
        return ApiResponse.success(responseDto, "OTP resent successfully.", null);
    }


//    @PostMapping("/register")
//    public ApiResponse<UsersResponseDTO,DefaultError> registerOrLogin(
//            @Valid @RequestBody UserRequestDto userRequestDto,
//            HttpServletResponse response) {
//        Object result = userService.registerOrLoginUser(userRequestDto, response);
//
//        if (result instanceof Users newUser) {
//            UsersResponseDTO responseDto = new UsersResponseDTO(newUser);
//            return ApiResponse.success(responseDto, "User registered successfully, OTP sent!",null);
//        } else if (result instanceof String message) {
//            return ApiResponse.success(null, message,null);
//        } else {
//            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(),"An unexpected error occurred.",null,null);
//        }
//    }
}


//sendOtp
//validation - lastOptTime to 1 min gap

//otpVerify
//limit validation max 5 time


//    @PostMapping("/verifyOtp")
//    public ApiResponse<String> verifyOtp(@RequestBody OtpRequestDto otpRequestDto, HttpServletResponse response) {
//        boolean isVerified = otpService.validateOtp(otpRequestDto.getUserIdentifier(), otpRequestDto.getOtp(), otpRequestDto.getMedium(), response);
//        if (isVerified) {
//            // Generate JWT Token here (make sure you have a method to create a token)
//            String jwtToken = jwtService.generateToken(otpRequestDto.getUserIdentifier());
//
//            // Set JWT token in the response header
//            response.setHeader("Authorization", "Bearer " + jwtToken);
//            return ApiResponse.success(null, "OTP verified successfully!");
//        } else {
//            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "OTP verification failed.", null);
//        }
//    }
//}