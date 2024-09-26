package com.auth.AuthImpl.ctp.entity;

import java.io.Serializable;
import java.util.Objects;

public class PlayerGameId implements Serializable {
    private Long userId;
    private Long gameId;

    public PlayerGameId() {}

    public PlayerGameId(Long userId, Long gameId) {
        this.userId = userId;
        this.gameId = gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerGameId that = (PlayerGameId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, gameId);
    }
}
