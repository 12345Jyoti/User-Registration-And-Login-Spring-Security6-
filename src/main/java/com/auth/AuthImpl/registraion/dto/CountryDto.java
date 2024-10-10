package com.auth.AuthImpl.registraion.dto;


import jakarta.validation.constraints.NotBlank;

public class CountryDto {

    private Long id;

    @NotBlank(message = "Country name is mandatory")
    private String name;

    // Constructors
    public CountryDto() {
    }

    public CountryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

