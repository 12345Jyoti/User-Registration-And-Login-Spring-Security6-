package com.application.Application.registraion;
import com.application.Application.registraion.dto.UsersRequestDTO;
import com.application.Application.registraion.dto.UsersResponseDTO;
import com.application.Application.registraion.entity.Users;
import com.application.Application.registraion.userServices.UserService;
import com.application.Application.utils.ApiResponse;
import org.apache.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<UsersResponseDTO>> registerUser(@RequestBody UsersRequestDTO userDto) throws Exception {
        UsersResponseDTO registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(ApiResponse.success(registeredUser, "User registered successfully"));
    }


    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String email, @RequestParam String otp) {
        boolean isVerified = userService.verifyEmailOtp(email, otp);
        if (isVerified) {
            return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.SC_BAD_REQUEST, "Invalid OTP", "Verification failed: Incorrect OTP"));
        }
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<String>> verifyPhone(@RequestParam String phoneNumber, @RequestParam String otp) {
        boolean isVerified = userService.verifyPhoneOtp(phoneNumber, otp);
        if (isVerified) {
            return ResponseEntity.ok(ApiResponse.success(null, "Phone number verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.SC_BAD_REQUEST, "Invalid OTP", "Verification failed: Incorrect OTP"));
        }
    }

    @PostMapping("/send-login-otp")
    public ResponseEntity<ApiResponse<String>> sendLoginOtp(@RequestParam String identifier) throws Exception {
        userService.sendLoginOtp(identifier);
        return ResponseEntity.ok(ApiResponse.success(null, "OTP sent to " + identifier));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginWithOtp(@RequestParam String identifier, @RequestParam String otp) throws Exception {
        String jwtToken = userService.loginWithOtp(identifier, otp);
        return ResponseEntity.ok(ApiResponse.success(jwtToken, "Login successful. JWT Token generated"));
    }

    @PostMapping("/login-with-username-password")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Users user) {
        String jwtToken = userService.verify(user);
        if ("Failed".equals(jwtToken)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.SC_BAD_REQUEST, "Login failed", "Invalid credentials"));
        }
        return ResponseEntity.ok(ApiResponse.success(jwtToken, "Login successful. JWT Token generated"));
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UsersResponseDTO>>> getAllUsers() {
        List<UsersResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "List of all users"));
    }
}

