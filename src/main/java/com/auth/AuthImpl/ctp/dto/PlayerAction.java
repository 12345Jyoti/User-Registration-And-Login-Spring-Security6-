package com.auth.AuthImpl.ctp.dto;

import com.auth.AuthImpl.ctp.enums.PlayerActionType;

public class PlayerAction {
    private Long playerId;
    private PlayerActionType action;
    private int betAmount;
    private Long gameId;
    private boolean isBlind; // Flag to indicate if the player is betting blind

    // Default constructor
    public PlayerAction() {
    }

    // Constructor with parameters for convenience
    public PlayerAction(Long playerId, PlayerActionType action, int betAmount, Long gameId, boolean isBlind) {
        this.playerId = playerId;
        this.action = action;
        this.betAmount = betAmount;
        this.gameId = gameId;
        this.isBlind = isBlind;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public PlayerActionType getAction() {
        return action;
    }

    public void setAction(PlayerActionType action) {
        this.action = action;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public boolean isBlind() {
        return isBlind; // Getter for isBlind flag
    }

    public void setBlind(boolean blind) {
        isBlind = blind; // Setter for isBlind flag
    }

    @Override
    public String toString() {
        return "PlayerAction{" +
                "playerId=" + playerId +
                ", action=" + action +
                ", betAmount=" + betAmount +
                ", gameId=" + gameId +
                ", isBlind=" + isBlind +
                '}';
    }
}
