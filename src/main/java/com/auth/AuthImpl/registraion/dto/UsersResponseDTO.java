package com.auth.AuthImpl.registraion.dto;

import com.auth.AuthImpl.registraion.dtos.request.UserRequestDto;
import com.auth.AuthImpl.registraion.entity.Users;

public class UsersResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String message;

    public UsersResponseDTO(){}

    public UsersResponseDTO(Users user, String message) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.message = message;
    }
    public UsersResponseDTO(UserRequestDto user) {
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.message = message;
    }

    public UsersResponseDTO(Long id, String username, String email, String phoneNumber, String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

//    public UsersResponseDTO(UserRequestDto user) {
//    }


    // Getters and Setters (if needed)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


//    public UsersResponseDTO(Users user) {       //Use userDto
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.email = user.getEmail();
//        this.phoneNumber = user.getPhoneNumber();
//    }


//import com.auth.AuthImpl.registraion.enums.Roles;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//
//public class UsersResponseDTO {
//
//    private Long id;
//    private String username;
//    private String email;
//    private String phoneNumber;
//    private boolean isEmailVerified;
//    private boolean isPhoneNumberVerified;
//    private Set<Roles> roles;
//    private String country;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//
//    public UsersResponseDTO(){}
//
//    public UsersResponseDTO(Long id, String username, String email, String phoneNumber, boolean isEmailVerified, boolean isPhoneNumberVerified, Set<Roles> roles, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.isEmailVerified = isEmailVerified;
//        this.isPhoneNumberVerified = isPhoneNumberVerified;
//        this.roles = roles;
//        this.country = country;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public boolean isEmailVerified() {
//        return isEmailVerified;
//    }
//
//    public void setEmailVerified(boolean emailVerified) {
//        isEmailVerified = emailVerified;
//    }
//
//    public boolean isPhoneNumberVerified() {
//        return isPhoneNumberVerified;
//    }
//
//    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
//        isPhoneNumberVerified = phoneNumberVerified;
//    }
//
//    public Set<Roles> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Set<Roles> roles) {
//        this.roles = roles;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//}

//
//import com.auth.AuthImpl.registraion.entity.Users;
//
//public class UsersResponseDTO {
//    private Long id;
//    private String username;
//    private String email;
//    private String phoneNumber;
//
//    public UsersResponseDTO(Users user) {       //Use userDto
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.email = user.getEmail();
//        this.phoneNumber = user.getPhoneNumber();
//    }
//
//    // Getters and Setters (if needed)
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//}

//import com.auth.AuthImpl.registraion.enums.Roles;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//
//public class UsersResponseDTO {
//
//    private Long id;
//    private String username;
//    private String email;
//    private String phoneNumber;
//    private boolean isEmailVerified;
//    private boolean isPhoneNumberVerified;
//    private Set<Roles> roles;
//    private String country;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//
//    public UsersResponseDTO(){}
//
//    public UsersResponseDTO(Long id, String username, String email, String phoneNumber, boolean isEmailVerified, boolean isPhoneNumberVerified, Set<Roles> roles, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.isEmailVerified = isEmailVerified;
//        this.isPhoneNumberVerified = isPhoneNumberVerified;
//        this.roles = roles;
//        this.country = country;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public boolean isEmailVerified() {
//        return isEmailVerified;
//    }
//
//    public void setEmailVerified(boolean emailVerified) {
//        isEmailVerified = emailVerified;
//    }
//
//    public boolean isPhoneNumberVerified() {
//        return isPhoneNumberVerified;
//    }
//
//    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
//        isPhoneNumberVerified = phoneNumberVerified;
//    }
//
//    public Set<Roles> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Set<Roles> roles) {
//        this.roles = roles;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//}
