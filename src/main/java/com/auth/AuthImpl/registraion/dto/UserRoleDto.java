package com.auth.AuthImpl.registraion.dto;


import jakarta.validation.constraints.NotBlank;

public class UserRoleDto {

    private Long id;

    @NotBlank(message = "Role name is mandatory")
    private String roleName;

    // Constructors
    public UserRoleDto() {
    }

    public UserRoleDto(Long id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

