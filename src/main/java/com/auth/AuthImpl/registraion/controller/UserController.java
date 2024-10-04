package com.auth.AuthImpl.registraion.controller;

import com.auth.AuthImpl.registraion.dtos.FormTypeRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.GetRegistrationFormRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.LoginRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.OtpVerificationRequestDto;
import com.auth.AuthImpl.registraion.dtos.response.GetRegistrationFormResponseDto;
import com.auth.AuthImpl.registraion.dtos.response.OtpVerificationResponseDto;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.FormType;
import com.auth.AuthImpl.registraion.userServices.LoginService;
import com.auth.AuthImpl.registraion.userServices.RegistrationFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users-register")
public class UserController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private RegistrationFormService registrationFormService;

    @GetMapping("/form")
    public ResponseEntity<GetRegistrationFormResponseDto> getRegistrationForm(@RequestBody FormTypeRequestDto formTypeDto) {
        // Extract the form type from the DTO, default to SIGNUP if not provided
        FormType formType = formTypeDto.getFormType();

        // Call service method to get the registration form based on form type
        GetRegistrationFormResponseDto formResponse = registrationFormService.getRegistrationForm(formType);

        return ResponseEntity.ok(formResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody GetRegistrationFormRequestDto registrationDto) {
        String email = registrationDto.getEmail().getEmail(); // Assuming you have email in the DTO
        String isdCode = registrationDto.getMobile().getIsdCode(); // Assuming this is set up in your DTO
        String phoneNumber = registrationDto.getMobile().getPhoneNumber(); // Assuming this is set up in your DTO
        Users registeredUser = registrationFormService.registerUser(email, isdCode, phoneNumber);

        return ResponseEntity.ok(("User registered successfully: " + registeredUser.getUsername()));
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody LoginRequestDto requestDto) {
        try {
            loginService.generateAndSendOtp(requestDto);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<OtpVerificationResponseDto> verifyOtp(@RequestBody OtpVerificationRequestDto requestDto) {
        System.out.println("Received OTP verification request: " + requestDto);
        OtpVerificationResponseDto responseDto = loginService.verifyOtp(requestDto);
        if (responseDto.isSuccess()) {
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }
    }

}

