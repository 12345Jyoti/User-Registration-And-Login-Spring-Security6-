package com.auth.AuthImpl.registraion.dto;


import jakarta.validation.constraints.*;

import java.util.List;

public class UserDto {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String isdCode;

    @NotBlank(message = "Phone number is mandatory")
    private String phoneNumber;

    private String password;

    @NotBlank(message = "Country cannot be null")
    private CountryDto country;

    private List<UserRoleDto> userRoles;

    // Constructors
    public UserDto() {
    }

    public UserDto(String username, String firstName, String lastName, String email,
                   String isdCode, String phoneNumber,String password, CountryDto country, List<UserRoleDto> userRoles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isdCode = isdCode;
        this.phoneNumber = phoneNumber;
        this.password=password;
        this.country = country;
        this.userRoles = userRoles;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsdCode() {
        return isdCode;
    }

    public void setIsdCode(String isdCode) {
        this.isdCode = isdCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CountryDto getCountry() {
        return country;
    }

    public void setCountry(CountryDto country) {
        this.country = country;
    }

    public List<UserRoleDto> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRoleDto> userRoles) {
        this.userRoles = userRoles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
