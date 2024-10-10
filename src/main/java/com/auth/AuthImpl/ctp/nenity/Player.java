package com.auth.AuthImpl.ctp.nenity;

import com.auth.AuthImpl.registraion.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Player")
public class Player extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;  // Reference to the user

    @Column(name = "player_name", nullable = false)
    private String playerName; // Player's name

    @Column(name = "total_available_amount", nullable = false)
    private BigDecimal totalAvailableAmount; // Total amount available for the player


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public BigDecimal getTotalAvailableAmount() {
        return totalAvailableAmount;
    }

    public void setTotalAvailableAmount(BigDecimal totalAvailableAmount) {
        // Ensuring total_available_amount is not negative
        if (totalAvailableAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total available amount cannot be negative");
        }
        this.totalAvailableAmount = totalAvailableAmount;
    }
}

