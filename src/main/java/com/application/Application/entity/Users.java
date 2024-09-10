package com.application.Application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private boolean isEmailVerified = false;
    private boolean isPhoneNumberVerified = false;

    private String emailOtp;
    private String phoneOtp;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id"), // Foreign key for Users
            inverseJoinColumns = @JoinColumn(name = "role_id") // Foreign key for Roles
    )
    private Set<Role> roles = new HashSet<>(); // Set of Role entities associated with the user

    @Column(nullable = false)
    private String country;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
