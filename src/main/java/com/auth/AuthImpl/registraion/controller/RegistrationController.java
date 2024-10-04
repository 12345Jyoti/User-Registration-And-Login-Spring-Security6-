//package com.auth.AuthImpl.registraion.controller;
//
//import com.auth.AuthImpl.registraion.dtos.request.GetRegistrationFormRequestDto;
//import com.auth.AuthImpl.registraion.userServices.RegistrationFormService;
//import com.auth.AuthImpl.registraion.entity.Users;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")  // Adjust the base URL as necessary
//public class RegistrationController {
//
//    @Autowired
//    private RegistrationFormService registrationFormService;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody GetRegistrationFormRequestDto registrationDto) {
//        // Extract data from DTO
//        String email = registrationDto.getEmail().getEmail(); // Assuming you have email in the DTO
//        String isdCode = registrationDto.getMobile().getIsdCode(); // Assuming this is set up in your DTO
//        String phoneNumber = registrationDto.getMobile().getPhoneNumber(); // Assuming this is set up in your DTO
//
//        // Register user
//        Users registeredUser = registrationFormService.registerUser(email, isdCode, phoneNumber);
//
//        return ResponseEntity.ok("User registered successfully: " + registeredUser.getUsername());
//    }
//}
