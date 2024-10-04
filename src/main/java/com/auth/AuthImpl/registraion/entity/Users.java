package com.auth.AuthImpl.registraion.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class Users extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String isdCode;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    // Constructors
    public Users() {
    }

    public Users(String username, String firstName, String lastName, String password,
                 String email, String isdCode, String phoneNumber, Country country) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.isdCode = isdCode;
        this.phoneNumber = phoneNumber;
        this.country = country;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
