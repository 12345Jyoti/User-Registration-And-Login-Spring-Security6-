//package com.auth.AuthImpl.registraion.userServices;
//
//import com.auth.AuthImpl.ctp.entity.Player;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.registraion.enums.Roles;
//import com.auth.AuthImpl.registraion.dto.UsersRequestDTO;
//import com.auth.AuthImpl.registraion.dto.UsersResponseDTO;
//import com.auth.AuthImpl.registraion.entity.Role;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.utils.JWTService;
//import com.auth.AuthImpl.registraion.mapper.UsersMapper;
//import com.auth.AuthImpl.registraion.otpServices.EmailService;
//import com.auth.AuthImpl.registraion.otpServices.SmsService;
//import com.auth.AuthImpl.registraion.repo.RoleRepository;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private SmsService smsService;
//
//    @Autowired
//    private JWTService jwtService;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
//    private final int OTP_LENGTH = 6;
//
//    @Override
//    public UsersResponseDTO registerUser(UsersRequestDTO usersRequestDTO) throws Exception {
//        try {
//            Optional<Users> existingUserByEmail = userRepository.findByEmail(usersRequestDTO.getEmail());
//            Optional<Users> existingUserByPhoneNumber = userRepository.findByPhoneNumber(usersRequestDTO.getPhoneNumber());
//
//            if (existingUserByEmail.isPresent()) {
//                throw new IllegalArgumentException("Email is already registered");
//            }
//
//            if (existingUserByPhoneNumber.isPresent()) {
//                throw new IllegalArgumentException("Phone number is already registered");
//            }
//
//            String emailOtp = generateOtp();
//            String phoneOtp = generateOtp();
//
//            Users user = UsersMapper.requestDtoToEntity(usersRequestDTO);
//
//            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//
//            user.setEmailOtp(emailOtp);
//            user.setPhoneOtp(phoneOtp);
//            user.setEmailVerified(false);
//            user.setPhoneNumberVerified(false);
//            user.setCreatedAt(LocalDateTime.now());
//
//            Set<Role> roles = new HashSet<>();
//            for (Roles roleEnum : usersRequestDTO.getRoles()) {
//                Role role = roleRepository.findByRoleName(roleEnum);
//                if (role == null) {
//                    role = new Role();
//                    role.setRoleName(roleEnum);
//                    role = roleRepository.save(role);
//                }
//                roles.add(role);
//            }
//            user.setRoles(roles);
//
//            Users savedUser = userRepository.save(user);
//
//            emailService.sendOtp(user.getEmail(), emailOtp);
//            smsService.sendOtp(user.getPhoneNumber(), phoneOtp);
//
//
//            Player player = new Player();
//            player.setUser(savedUser); // Set the user reference
//            player.setChips(1000L); // Default chips
//            player.setPlayerName(savedUser.getUsername()); // Store username as playerName
//            player.setPlayerRank("Beginner"); // Assign default rank
//
//            playerRepository.save(player);
//
//            return UsersMapper.entityToResponseDto(savedUser);
//        } catch (Exception e) {
//            logger.error("Error occurred while registering user: {}", e.getMessage());
//            throw e;
//        }
//    }
//
//    @Override
//    public boolean verifyEmailOtp(String email, String otp) {
//        try {
//            Optional<Users> userOptional = userRepository.findByEmail(email);
//            if (userOptional.isPresent()) {
//                Users user = userOptional.get();
//                if (user.getEmailOtp().equals(otp)) {
//                    user.setEmailVerified(true);
//                    user.setEmailOtp(null);
//                    userRepository.save(user);
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error verifying email OTP: {}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public boolean verifyPhoneOtp(String phoneNumber, String otp) {
//        try {
//            Optional<Users> userOptional = userRepository.findByPhoneNumber(phoneNumber);
//            if (userOptional.isPresent()) {
//                Users user = userOptional.get();
//                if (otp.equals(user.getPhoneOtp())) {
//                    user.setPhoneNumberVerified(true);
//                    user.setPhoneOtp(null);
//                    userRepository.save(user);
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error verifying phone OTP: {}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public void sendLoginOtp(String identifier) throws Exception {
//        try {
//            Optional<Users> userOptional = userRepository.findByEmail(identifier);
//            if (userOptional.isEmpty()) {
//                userOptional = userRepository.findByPhoneNumber(identifier);
//            }
//
//            if (userOptional.isEmpty()) {
//                throw new IllegalArgumentException("User not found with provided email or phone number");
//            }
//
//            Users user = userOptional.get();
//            String otp = generateOtp();
//
//            if (identifier.contains("@")) {
//                user.setEmailOtp(otp);
//                userRepository.save(user);
//                emailService.sendOtp(user.getEmail(), otp);
//            } else {
//                user.setPhoneOtp(otp);
//                userRepository.save(user);
//                smsService.sendOtp(user.getPhoneNumber(), otp);
//            }
//        } catch (Exception e) {
//            logger.error("Error sending login OTP: {}", e.getMessage());
//            throw new Exception("Failed to send login OTP");
//        }
//    }
//
//    @Override
//    public String loginWithOtp(String identifier, String otp) throws Exception {
//        try {
//            Optional<Users> userOptional = userRepository.findByEmail(identifier);
//            if (userOptional.isEmpty()) {
//                userOptional = userRepository.findByPhoneNumber(identifier);
//            }
//
//            if (userOptional.isEmpty()) {
//                throw new IllegalArgumentException("User not found with provided email or phone number");
//            }
//
//            Users user = userOptional.get();
//            boolean isOtpValid = false;
//
//            if (identifier.contains("@")) {
//                if (otp.equals(user.getEmailOtp())) {
//                    user.setEmailOtp(null);
//                    isOtpValid = true;
//                }
//            } else {
//                if (otp.equals(user.getPhoneOtp())) {
//                    user.setPhoneOtp(null);
//                    isOtpValid = true;
//                }
//            }
//
//            if (!isOtpValid) {
//                throw new IllegalArgumentException("Invalid OTP");
//            }
//
//            String jwtToken = jwtService.generateToken(user.getUsername());
//            userRepository.save(user);
//
//            return jwtToken;
//        } catch (IllegalArgumentException e) {
//            logger.error("Invalid login OTP: {}", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error during OTP login: {}", e.getMessage());
//            throw new Exception("Login with OTP failed");
//        }
//    }
//
//    @Override
//    public String verify(Users user) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//
//            if (authentication.isAuthenticated()) {
//                return jwtService.generateToken(user.getUsername());
//            }
//        } catch (Exception e) {
//            logger.error("Error during user verification: {}", e.getMessage());
//        }
//        return "Failed";
//    }
//
//    @Override
//    public List<UsersResponseDTO> getAllUsers() {
//        try {
//            List<Users> usersList = userRepository.findAll();
//            return usersList.stream().map(UsersMapper::entityToResponseDto).toList();
//        } catch (Exception e) {
//            logger.error("Error occurred while fetching users: {}", e.getMessage());
//            throw new RuntimeException("Failed to fetch users");
//        }
//    }
//
//    private String generateOtp() {
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//        for (int i = 0; i < OTP_LENGTH; i++) {
//            otp.append(random.nextInt(10));
//        }
//        return otp.toString();
//    }
//}
