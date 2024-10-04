package com.auth.AuthImpl.registraion.entity;

import com.auth.AuthImpl.registraion.enums.OtpType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification")
public class OtpVerification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key column
    private Users user;
    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private boolean isUsed = false;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private OtpType type;

    // Getters and Setters
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OtpType getType() {
        return type;
    }

    public void setType(OtpType type) {
        this.type = type;
    }
}
