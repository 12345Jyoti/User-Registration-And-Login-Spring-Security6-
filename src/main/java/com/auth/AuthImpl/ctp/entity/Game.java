package com.auth.AuthImpl.ctp.entity;


import com.auth.AuthImpl.ctp.enums.GameStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    private GameStatus status;


    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Long totalPot;

    @Column(nullable = false)
    private int currentPlayerCount = 0; // Track current number of players

    @Column(nullable = false)
    private final int maxPlayers = 5; // Maximum players allowed

    @Column(nullable = false)
    private final int minPlayers = 2; // Minimum players required to start


    public Game() {}



    public Long getGameId() {
        return gameId;
    }

    public Game(Long gameId, GameStatus status, LocalDateTime startTime, LocalDateTime endTime, Long totalPot, int currentPlayerCount) {
        this.gameId = gameId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPot = totalPot;
        this.currentPlayerCount = currentPlayerCount;
    }

    public int getCurrentPlayerCount() {
        return currentPlayerCount;
    }

    public void setCurrentPlayerCount(int currentPlayerCount) {
        this.currentPlayerCount = currentPlayerCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getTotalPot() {
        return totalPot;
    }

    public void setTotalPot(Long totalPot) {
        this.totalPot = totalPot;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus sameStatus) {
        this.status = sameStatus;
    }
}

