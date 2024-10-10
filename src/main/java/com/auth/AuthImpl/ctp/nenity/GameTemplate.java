package com.auth.AuthImpl.ctp.nenity;

import com.auth.AuthImpl.registraion.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "game_template")
public class GameTemplate extends BaseEntity {

    @Column(name = "initial_bet_amount", nullable = false)
    private BigDecimal initialBetAmount;

    @Column(name = "max_bet_limit", nullable = false)
    private BigDecimal maxBetLimit;

    @Column(name = "min_required_players", nullable = false)
    private Integer minRequiredPlayers;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "initial_active_table_count", nullable = false)
    private Integer initialActiveTableCount; // Default value can be set in service/constructor if needed

    @Column(name = "max_active_table_count", nullable = false)
    private Integer maxActiveTableCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_temp_status", nullable = false)
    private GameStatus gameTempStatus; // Updated to match database field

    // Getters and Setters

    public BigDecimal getInitialBetAmount() {
        return initialBetAmount;
    }

    public void setInitialBetAmount(BigDecimal initialBetAmount) {
        this.initialBetAmount = initialBetAmount;
    }

    public BigDecimal getMaxBetLimit() {
        return maxBetLimit;
    }

    public void setMaxBetLimit(BigDecimal maxBetLimit) {
        this.maxBetLimit = maxBetLimit;
    }

    public Integer getMinRequiredPlayers() {
        return minRequiredPlayers;
    }

    public void setMinRequiredPlayers(Integer minRequiredPlayers) {
        this.minRequiredPlayers = minRequiredPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getInitialActiveTableCount() {
        return initialActiveTableCount;
    }

    public void setInitialActiveTableCount(Integer initialActiveTableCount) {
        this.initialActiveTableCount = initialActiveTableCount;
    }

    public Integer getMaxActiveTableCount() {
        return maxActiveTableCount;
    }

    public void setMaxActiveTableCount(Integer maxActiveTableCount) {
        this.maxActiveTableCount = maxActiveTableCount;
    }

    public GameStatus getGameTempStatus() {
        return gameTempStatus;
    }

    public void setGameTempStatus(GameStatus gameTempStatus) {
        this.gameTempStatus = gameTempStatus;
    }

    // Enum for Game Status, updated to match database
    public enum GameStatus {
        active,     // lowercase to match the database value
        inactive,   // lowercase to match the database value
        deleted     // lowercase to match the database value
    }

}
