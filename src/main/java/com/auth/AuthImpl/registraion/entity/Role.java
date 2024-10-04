package com.auth.AuthImpl.registraion.entity;

import com.auth.AuthImpl.registraion.enums.Roles;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    @Column(nullable = false, unique = true)
    private Roles roleName; // Change this field to use the enum

    // Constructors
    public Role() {
    }

    public Role(Roles roleName) {
        this.roleName = roleName;
    }

    // Getters and Setters
    public Roles getRoleName() {
        return roleName;
    }

    public void setRoleName(Roles roleName) {
        this.roleName = roleName;
    }

}

