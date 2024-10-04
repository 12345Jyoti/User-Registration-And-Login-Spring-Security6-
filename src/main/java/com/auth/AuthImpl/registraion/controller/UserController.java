package com.auth.AuthImpl.registraion.controller;

import com.auth.AuthImpl.registraion.dtos.FormTypeRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.GetRegistrationFormRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.LoginRequestDto;
import com.auth.AuthImpl.registraion.dtos.request.LoginRequestDtoWithPass;
import com.auth.AuthImpl.registraion.dtos.request.OtpVerificationRequestDto;
import com.auth.AuthImpl.registraion.dtos.response.GetRegistrationFormResponseDto;
import com.auth.AuthImpl.registraion.dtos.response.OtpVerificationResponseDto;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.FormType;
import com.auth.AuthImpl.registraion.userServices.LoginService;
import com.auth.AuthImpl.registraion.userServices.RegistrationFormService;
import com.auth.AuthImpl.utils.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth-user")
public class UserController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private RegistrationFormService registrationFormService;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/form")
    public ResponseEntity<GetRegistrationFormResponseDto> getRegistrationForm(@RequestBody FormTypeRequestDto formTypeDto) {
        FormType formType = formTypeDto.getFormType();
        GetRegistrationFormResponseDto formResponse = registrationFormService.getRegistrationForm(formType);

        return ResponseEntity.ok(formResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody GetRegistrationFormRequestDto registrationDto) {
        String email = registrationDto.getEmail().getEmailAddress(); // Assuming you have email in the DTO
        String isdCode = registrationDto.getMobile().getIsdCode(); // Assuming this is set up in your DTO
        String phoneNumber = registrationDto.getMobile().getPhoneNumber(); // Assuming this is set up in your DTO
        Users registeredUser = registrationFormService.registerUser(email, isdCode, phoneNumber);

        return ResponseEntity.ok(("User registered successfully: " + registeredUser.getUsername()));
    }

    @PostMapping("/login")
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


    @PostMapping("/login-with-pass")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequestDtoWithPass loginRequestDto) {
        try {
            // Extract username and password from the DTO
            String username = loginRequestDto.getUsername();
            String password = loginRequestDto.getPassword();

            // Validate user credentials
            boolean isValidUser = loginService.validateUserCredentials(username, password);

            if (isValidUser) {
                // Generate JWT Token for the user
                String jwtToken = jwtService.generateToken(username);

                // Prepare response with the token
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful!");
                response.put("token", jwtToken);

                return ResponseEntity.ok(response);
            } else {
                // Return error response if credentials are invalid
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
            }
        } catch (Exception e) {
            // Return a generic error response if any other exception occurs
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred during login"));
        }

}}

