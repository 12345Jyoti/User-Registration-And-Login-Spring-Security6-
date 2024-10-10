package com.auth.AuthImpl.ctp.nenity;

import com.auth.AuthImpl.registraion.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "GameResult")
public class GameResult extends BaseEntity {

    @Column(name = "game_id", nullable = false)
    private Long gameId;  // Foreign key referencing the Game table

    @Column(name = "winning_player_id", nullable = false)
    private Integer winningPlayerId;  // Foreign key referencing the Player table

    @Column(name = "winning_amount", nullable = false)
    private BigDecimal winningAmount;  // Amount won by the winning player

    // Getters and Setters

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {

        this.gameId = gameId;
    }

    public Integer getWinningPlayerId() {
        return winningPlayerId;
    }

    public void setWinningPlayerId(Integer winningPlayerId) {
        this.winningPlayerId = winningPlayerId;
    }

    public BigDecimal getWinningAmount() {
        return winningAmount;
    }

    public void setWinningAmount(BigDecimal winningAmount) {
        this.winningAmount = winningAmount;
    }

}

