package com.auth.AuthImpl.registraion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country extends BaseEntity {

    @Column(nullable = false)
    private String countryName;

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String isdCode;

    // Getters and Setters
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsdCode() {
        return isdCode;
    }

    public void setIsdCode(String isdCode) {
        this.isdCode = isdCode;
    }
}

