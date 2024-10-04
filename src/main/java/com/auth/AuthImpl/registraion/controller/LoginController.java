//package com.auth.AuthImpl.registraion.controller;
//
//import com.auth.AuthImpl.registraion.dtos.request.LoginRequestDto;
//import com.auth.AuthImpl.registraion.dtos.request.OtpVerificationRequestDto;
//import com.auth.AuthImpl.registraion.dtos.response.OtpVerificationResponseDto;
//import com.auth.AuthImpl.registraion.userServices.LoginService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/login")
//public class LoginController {
//
//    @Autowired
//    private LoginService loginService;
//
//    // Endpoint to generate and send OTP
//    @PostMapping("/generate-otp")
//    public ResponseEntity<String> generateOtp(@RequestBody LoginRequestDto requestDto) {
//        try {
//            loginService.generateAndSendOtp(requestDto);
//            return ResponseEntity.ok("OTP sent successfully.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }
//
//    // Endpoint to verify OTP
//    @PostMapping("/verify-otp")
//    public ResponseEntity<OtpVerificationResponseDto> verifyOtp(@RequestBody OtpVerificationRequestDto requestDto) {
//        OtpVerificationResponseDto responseDto = loginService.verifyOtp(requestDto);
//
//        if (responseDto.isSuccess()) {
//            return ResponseEntity.ok(responseDto);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
//        }
//    }
//}
