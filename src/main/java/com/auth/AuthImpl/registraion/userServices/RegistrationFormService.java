//package com.auth.AuthImpl.registraion.userServices;
//
//import com.auth.AuthImpl.registraion.dtos.Email;
//import com.auth.AuthImpl.registraion.dtos.Mobile;
//import com.auth.AuthImpl.registraion.dtos.response.GetRegistrationFormResponseDto;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.enums.FormType;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // For password hashing
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class RegistrationFormService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // Password encoder to hash passwords
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    // Inject the active registration options from properties
//    @Value("${registration.options.active}")
//    private String activeOptions;
//
//    public GetRegistrationFormResponseDto getRegistrationForm(FormType formType) {
//        GetRegistrationFormResponseDto response = new GetRegistrationFormResponseDto();
//
//        // Parse active options from configuration
//        List<String> activeOptionList = Arrays.asList(activeOptions.split(","));
//
//        // Get the primary active option
//        String primaryActiveOption = activeOptionList.get(0);  // Pick the first option
//
//        // Set the form based on the form type
//        if (formType == FormType.SIGNUP) {
//            response.setOptions(Arrays.asList(primaryActiveOption));
//
//            switch (primaryActiveOption) {
//                case "mobile":
//                    Mobile mobileForm = new Mobile();
//                    mobileForm.setIsdCode("+1");  // Default ISD code for the US (example, can be empty or dynamic)
//                    mobileForm.setPhoneNumber("");  // Placeholder for the user to fill in their number
//                    mobileForm.setInputType("tel");  // Input type "tel" for phone numbers
//                    mobileForm.setMandatory(true);  // Make it mandatory
//                    mobileForm.setValidationType("true");
//                    mobileForm.setPlaceHolder("Enter your phone number");  // Placeholder text
//
//                    response.setMobile(mobileForm);  // Set the mobile form in the response
//                    break;
//
//                case "email":
//                    Email emailField = new Email();
//                    emailField.setInputType("email");
//                    emailField.setMandatory(true);
//                    emailField.setValidationType("true");
//                    emailField.setPlaceHolder("Enter your email address");
//                    response.setEmail(emailField);
//                    break;
//
//                // Other cases like "google" or "truecaller" can be handled similarly
//            }
//        } else if (formType == FormType.LOGIN) {
//
//            // Logic for login form (if needed)
//        }
//
//        return response;
//    }
//
//    public Users registerUser(String email, String isdCode, String phoneNumber) {
//        // Check if user already exists
//        if (userRepository.findByEmail(email) != null) {
//            throw new RuntimeException("Email already in use");
//        }
//
//        // Create a new user instance
//        Users user = new Users();
//
//        // Extract username from email (the part before the @)
//        String username = email.split("@")[0]; // Get the part before the '@' symbol
//        user.setUsername(username); // Set the username
//
//        // Set other user properties
//        user.setEmail(email);
//        user.setIsdCode(isdCode);
//        user.setPhoneNumber(phoneNumber);
//        user.setCreatedBy("SYSTEM");  // Set created by system for auditing
//
//        return userRepository.save(user);
//    }
//}
package com.auth.AuthImpl.registraion.userServices;

import com.auth.AuthImpl.registraion.dtos.Email;
import com.auth.AuthImpl.registraion.dtos.Mobile;
import com.auth.AuthImpl.registraion.dtos.response.GetRegistrationFormResponseDto;
import com.auth.AuthImpl.registraion.dtos.response.LoginResponseDto;
import com.auth.AuthImpl.registraion.entity.Users;
import com.auth.AuthImpl.registraion.enums.FormType;
import com.auth.AuthImpl.registraion.enums.Medium;
import com.auth.AuthImpl.registraion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationFormService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Load active registration/login options from properties (e.g., "email,mobile")
    @Value("${registration.options.active}")
    private String activeOptions;

    /**
     * Method to handle both SIGNUP and LOGIN form generation.
     * @param formType either SIGNUP or LOGIN
     * @return form response containing the available options
     */
    public GetRegistrationFormResponseDto getRegistrationForm(FormType formType) {
        // Parse active options from properties
        List<String> activeOptionList = Arrays.asList(activeOptions.split(","));

        // Common response object
        GetRegistrationFormResponseDto response = new GetRegistrationFormResponseDto();

        if (formType == FormType.SIGNUP) {
            buildSignupFormResponse(activeOptionList, response);
        } else if (formType == FormType.LOGIN) {
            buildLoginFormResponse(activeOptionList, response);
        } else {
            throw new IllegalArgumentException("Unsupported form type: " + formType);
        }

        return response;
    }

    /**
     * Builds the SIGNUP form response.
     * @param activeOptionList List of active registration options (from properties)
     * @return response with the signup form details
     */
    private void buildSignupFormResponse(List<String> activeOptionList, GetRegistrationFormResponseDto response) {
        String primaryActiveOption = activeOptionList.get(0);  // Use first option as primary
        response.setOptions(Arrays.asList(primaryActiveOption));

        switch (primaryActiveOption) {
            case "mobile":
                response.setMobile(buildMobileForm());
                break;

            case "email":
                response.setEmail(buildEmailForm());
                break;

            // Add more options like Google/Truecaller if necessary
            default:
                throw new IllegalArgumentException("Invalid signup option: " + primaryActiveOption);
        }
    }

    /**
     * Builds the LOGIN form response.
     * @param activeOptionList List of active login options (from properties)
     * @param response The response object to be populated
     */
    private void buildLoginFormResponse(List<String> activeOptionList, GetRegistrationFormResponseDto response) {
        List<Medium> mediums = activeOptionList.stream()
                .map(this::mapToMedium)
                .collect(Collectors.toList());

        response.setOptions(mediums.stream().map(Medium::name).collect(Collectors.toList()));

        if (mediums.contains(Medium.MOBILE)) {
            response.setMobile(buildMobileForm());
        } else if (mediums.contains(Medium.EMAIL)) {
            response.setEmail(buildEmailForm());
        }
    }

    /**
     * Maps option string to Medium enum.
     * @param option registration/login option as a string
     * @return mapped Medium enum
     */
    /**
     * Maps option string to Medium enum.
     * @param option registration/login option as a string
     * @return mapped Medium enum
     */
    private Medium mapToMedium(String option) {
        switch (option.toLowerCase()) {
            case "email":
                return Medium.EMAIL;
            case "mobile":
                return Medium.MOBILE;
            // Handle other options like Google, Truecaller here
            default:
                throw new IllegalArgumentException("Unsupported medium: " + option);
        }
    }

    /**
     * Builds the mobile form with default settings.
     * @return Mobile form object
     */
    private Mobile buildMobileForm() {
        Mobile mobileForm = new Mobile();
        mobileForm.setIsdCode("+1"); // Default ISD code, can be dynamic
        mobileForm.setPhoneNumber("");
        mobileForm.setInputType("tel");
        mobileForm.setMandatory(true);
        mobileForm.setValidationType("true");
        mobileForm.setPlaceHolder("Enter your phone number");
        return mobileForm;
    }

    /**
     * Builds the email form with default settings.
     * @return Email form object
     */
    private Email buildEmailForm() {
        Email emailForm = new Email();
        emailForm.setInputType("email");
        emailForm.setMandatory(true);
        emailForm.setValidationType("true");
        emailForm.setPlaceHolder("Enter your email address");
        return emailForm;
    }

    /**
     * Register a new user based on the provided email and phone details.
     * @param email user's email address
     * @param isdCode international dialing code for phone number
     * @param phoneNumber user's phone number
     * @return the newly registered user
     */
    public Users registerUser(String email, String isdCode, String phoneNumber) {
        Optional<Users> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Users user = new Users();
        user.setUsername(extractUsernameFromEmail(email));
        user.setEmail(email);
        user.setIsdCode(isdCode);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode("defaultPassword"));  // Set a default password or prompt user for one
        user.setCreatedBy("SYSTEM");

        return userRepository.save(user);
    }

    /**
     * Extracts the username from the given email.
     * @param email the user's email address
     * @return username (part before the '@' symbol)
     */
    private String extractUsernameFromEmail(String email) {
        return email.split("@")[0];
    }
}
