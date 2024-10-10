package com.auth.AuthImpl.ctp.nenity;

import com.auth.AuthImpl.ctp.enums.*;
import com.auth.AuthImpl.ctp.enums.PlayerGameResult;
import com.auth.AuthImpl.registraion.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "GamePlayer")
public class GamePlayer extends BaseEntity {

    @Column(name = "game_id", nullable = false)
    private Long gameId;  // Foreign key referencing the Game table

    @Column(name = "player_id", nullable = false)
    private Long playerId;  // Foreign key referencing the Player table

    @Column(name = "game_current_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameCurrentStatus gameCurrentStatus;  // Current status of the player in the game

        @Column(name = "cards")
        private String cards;  // JSON field for player's cards

    @Column(name = "player_betting_amount", nullable = false)
    @DecimalMin("0.0")  // Ensures non-negative betting amount
    private BigDecimal playerBettingAmount;  // Player's betting amount

    @Column(name = "joined_at", nullable = false)
    private Date joinedAt;  // Timestamp when the player joined

    @Column(insertable=false, updatable=false)    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;  // Status of the game player

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private PlayerGameResult result;  // Result of the game for the player

    // Getters and Setters

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public GameCurrentStatus getGameCurrentStatus() {
        return gameCurrentStatus;
    }

    public void setGameCurrentStatus(GameCurrentStatus gameCurrentStatus) {
        this.gameCurrentStatus = gameCurrentStatus;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public BigDecimal getPlayerBettingAmount() {
        return playerBettingAmount;
    }

    public void setPlayerBettingAmount(BigDecimal playerBettingAmount) {
        this.playerBettingAmount = playerBettingAmount;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public PlayerGameResult getResult() {
        return result;
    }

    public void setResult(PlayerGameResult result) {
        this.result = result;
    }

//    // Enums for status
//    public enum CurrStatus {
//        BLIND,
//        SEEN,
//        FOLD
//    }
//
//    public enum Status {
//        ACTIVE,
//        INACTIVE,
//        DISQUALIFIED
//    }
//
//    public enum Result {
//        WIN,
//        LOSE,
//        ELIMINATED
//    }
}

