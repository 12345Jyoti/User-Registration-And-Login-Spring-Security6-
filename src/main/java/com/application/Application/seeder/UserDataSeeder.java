//package com.application.Application.seeder;
//
//
//import com.application.Application.entity.Country;
//import com.application.Application.entity.Users;
//import com.application.Application.repo.CountryRepository;
//import com.application.Application.repo.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CountryRepository countryRepository; // Assuming you have a Country repository
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
////        seedUsers();
//    }
//
//    private void seedUsers() {
//        // Sample country (assuming you have at least one country in your Country table)
//
//        // Create sample users
//        if (country != null) {
//            Users user1 = new Users(null, "johnDoe", passwordEncoder.encode("securePassword123"),
//                    "john.doe@example.com", "1234567890", "country");
//            user1.setEmailVerified(false);
//            user1.setPhoneNumberVerified(false);
//            user1.setEmailOtp("123456");
//            user1.setPhoneOtp("654321");
//            userRepository.save(user1);
//
//            Users user2 = new Users(null, "janeDoe", passwordEncoder.encode("anotherPassword456"),
//                    "jane.doe@example.com", "0987654321", "country");
//            user2.setEmailVerified(false);
//            user2.setPhoneNumberVerified(false);
//            user2.setEmailOtp("234567");
//            user2.setPhoneOtp("765432");
//            userRepository.save(user2);
//
//            Users user3 = new Users(null, "aliceSmith", passwordEncoder.encode("alicePassword789"),
//                    "alice.smith@example.com", "1112223333", "country");
//            user3.setEmailVerified(false);
//            user3.setPhoneNumberVerified(false);
//            user3.setEmailOtp("345678");
//            user3.setPhoneOtp("876543");
//            userRepository.save(user3);
//        } else {
//            System.out.println("No country found to associate with users.");
//        }
//
//        System.out.println("Sample users have been seeded into the database.");
//    }
//
//}
//
package com.application.Application.seeder;

import com.application.Application.dto.UsersDTO;
import com.application.Application.common.enums.Role;
import com.application.Application.entity.Users;
import com.application.Application.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserDataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
    }

    private void seedUsers() {
        Users user1 = createUser("john_doe", "password123", "john@example.com", "+1234567890", true, true, "USA", "123456", "654321");
        Users user2 = createUser("jane_smith", "password456", "jane@example.com", "+0987654321", true, true, "Canada", "654321", "123456");
        Users user3 = createUser("alice_jones", "password789", "alice@example.com", "+1122334455", true, true, "UK", "789012", "210987");

        // Save users to the database
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    private Users createUser(String username, String password, String email, String phoneNumber, boolean isEmailVerified, boolean isPhoneNumberVerified, String country, String emailOtp, String phoneOtp) {
        Users user = new Users();
        user.setId(null); // Assuming ID is auto-generated
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setEmailVerified(isEmailVerified);
        user.setPhoneNumberVerified(isPhoneNumberVerified);
        user.setCountry(country);
        user.setEmailOtp(emailOtp);
        user.setPhoneOtp(phoneOtp);
        user.setRoles(getDefaultRoles()); // Set default roles
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER); // Add your default roles here
        return roles;
    }
}
