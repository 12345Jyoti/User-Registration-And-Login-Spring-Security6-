package com.application.Application.service;

import com.application.Application.entity.Users;
import com.application.Application.repo.UserRepo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    private  BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder(12);

    public Users saveUser(Users user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public String verify(Users user) {
        Authentication authentication=
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword()));

       if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getUserName());
       return "Failed";
    }
}
//package com.application.Application.service;
//
//import com.application.Application.entity.Users;
//import com.application.Application.repo.UserRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Autowired
//    private JWTService jwtService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12); // Use the BCrypt encoder with strength 12
//
//    // Save a user with an encrypted password
//    public Users saveUser(Users user) {
//        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // Hash the password before saving
//        return userRepo.save(user);
//    }
//
//    // Verify user credentials and generate JWT token if successful
//    public String verify(Users user) {
//        // Authenticate the user with the provided username and password
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
//        );
//
//        // If authentication is successful, generate and return a JWT token
//        if (authentication.isAuthenticated()) {
//            return jwtService.generateToken(user.getUserName());
//        }
//
//        // If authentication fails, return a failure message
//        return "Authentication failed";
//    }
//}
