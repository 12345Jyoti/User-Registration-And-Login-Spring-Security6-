package com.application.Application.controller;
import com.application.Application.dto.UsersDTO;
import com.application.Application.entity.Users;
import com.application.Application.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UsersDTO> registerUser(@RequestBody UsersDTO userDto) throws Exception {
        UsersDTO registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(registeredUser);
    }


    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String otp) {
        boolean isVerified = userService.verifyEmailOtp(email, otp);
        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestParam String phoneNumber, @RequestParam String otp) {
        boolean isVerified = userService.verifyPhoneOtp(phoneNumber, otp);
        if (isVerified) {
            return ResponseEntity.ok("Phone number verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

    @PostMapping("/send-login-otp")
    public ResponseEntity<String> sendLoginOtp(@RequestParam String identifier) throws Exception {
        userService.sendLoginOtp(identifier);
        return ResponseEntity.ok("OTP sent to " + identifier);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginWithOtp(@RequestParam String identifier, @RequestParam String otp) throws Exception {
        String jwtToken = userService.loginWithOtp(identifier, otp);
        return ResponseEntity.ok("Login successful. JWT Token: " + jwtToken);
    }

    @PostMapping("/login-with-username-password")
    public ResponseEntity<String> login(@RequestBody Users user) {
        String jwtToken = userService.verify(user);
        return ResponseEntity.ok("Login successful. JWT Token: " + jwtToken);
    }

//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Restrict access to only users with ROLE_ADMIN
    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        List<UsersDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
