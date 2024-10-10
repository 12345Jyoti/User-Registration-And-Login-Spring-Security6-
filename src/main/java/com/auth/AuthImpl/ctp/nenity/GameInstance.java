//package com.auth.AuthImpl.ctp.nenity;
//
//import com.auth.AuthImpl.registraion.entity.BaseEntity;
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "game_instance")
//public class GameInstance extends BaseEntity {
//
//    @ManyToOne
//    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = false)
//    private GameTemplate gameTemplate;
//
//    @Column(name = "max_bet_limit", nullable = false)
//    private BigDecimal maxBetLimit;
//
//    @Column(name = "current_bet", nullable = false)
//    private BigDecimal currentBet = BigDecimal.ZERO; // Default value
//
//    @Column(name = "current_player_index")
//    private Integer currentPlayerIndex; // Defaults to NULL
//
//    @Column(name = "prev_player_index")
//    private Integer prevPlayerIndex; // Defaults to NULL
//
//    @Column(name = "next_player_index")
//    private Integer nextPlayerIndex; // Defaults to NULL
//
//    @Column(name = "total_joined_players", nullable = false)
//    private Integer totalJoinedPlayers = 0; // Default value
//
//    @Column(name = "total_playing_players", nullable = false)
//    private Integer totalPlayingPlayers = 0; // Default value
//
//    @Column(name = "total_waiting_players", nullable = false)
//    private Integer totalWaitingPlayers = 0; // Default value
//
//    @Column(name = "start_time", nullable = false)
//    private LocalDateTime startTime = LocalDateTime.now(); // Default value
//
//    @Column(name = "end_time")
//    private LocalDateTime endTime; // Defaults to NULL
//
//    @Enumerated(EnumType.STRING)
//    @Column(insertable=false, updatable=false)
//    private GameStatus gameStatus = GameStatus.active; // Default value
//
//    // Getters and Setters
//
//    public GameTemplate getGameTemplate() {
//        return gameTemplate;
//    }
//
//    public void setGameTemplate(GameTemplate gameTemplate) {
//        this.gameTemplate = gameTemplate;
//    }
//
//    public BigDecimal getMaxBetLimit() {
//        return maxBetLimit;
//    }
//
//    public void setMaxBetLimit(BigDecimal maxBetLimit) {
//        this.maxBetLimit = maxBetLimit;
//    }
//
//    public BigDecimal getCurrentBet() {
//        return currentBet;
//    }
//
//    public void setCurrentBet(BigDecimal currentBet) {
//        this.currentBet = currentBet;
//    }
//
//    public Integer getCurrentPlayerIndex() {
//        return currentPlayerIndex;
//    }
//
//    public void setCurrentPlayerIndex(Integer currentPlayerIndex) {
//        this.currentPlayerIndex = currentPlayerIndex;
//    }
//
//    public Integer getPrevPlayerIndex() {
//        return prevPlayerIndex;
//    }
//
//    public void setPrevPlayerIndex(Integer prevPlayerIndex) {
//        this.prevPlayerIndex = prevPlayerIndex;
//    }
//
//    public Integer getNextPlayerIndex() {
//        return nextPlayerIndex;
//    }
//
//    public void setNextPlayerIndex(Integer nextPlayerIndex) {
//        this.nextPlayerIndex = nextPlayerIndex;
//    }
//
//    public Integer getTotalJoinedPlayers() {
//        return totalJoinedPlayers;
//    }
//
//    public void setTotalJoinedPlayers(Integer totalJoinedPlayers) {
//        this.totalJoinedPlayers = totalJoinedPlayers;
//    }
//
//    public Integer getTotalPlayingPlayers() {
//        return totalPlayingPlayers;
//    }
//
//    public void setTotalPlayingPlayers(Integer totalPlayingPlayers) {
//        this.totalPlayingPlayers = totalPlayingPlayers;
//    }
//
//    public Integer getTotalWaitingPlayers() {
//        return totalWaitingPlayers;
//    }
//
//    public void setTotalWaitingPlayers(Integer totalWaitingPlayers) {
//        this.totalWaitingPlayers = totalWaitingPlayers;
//    }
//
//
//    public LocalDateTime getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(LocalDateTime startTime) {
//        this.startTime = startTime;
//    }
//
//    public LocalDateTime getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(LocalDateTime endTime) {
//        this.endTime = endTime;
//    }
//
//    public GameStatus getGameStatus() {
//        return gameStatus;
//    }
//
//    public void setGameStatus(GameStatus gameStatus) {
//        this.gameStatus = gameStatus;
//    }
//
//    // Enum for Game Status
//    public enum GameStatus {
//        active, waitingForPlayer, completed
//    }
//}

package com.auth.AuthImpl.ctp.nenity;

import com.auth.AuthImpl.registraion.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_instance")
public class GameInstance extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = false)
    private GameTemplate gameTemplate;

    @Column(name = "max_bet_limit", nullable = false)
    private BigDecimal maxBetLimit;

    @Column(name = "current_bet", nullable = false)
    private BigDecimal currentBet = BigDecimal.ZERO; // Default value

    // NEW FIELD: Adding currentPot to store the total amount bet in the game
    @Column(name = "current_pot", nullable = false)
    private BigDecimal currentPot = BigDecimal.ZERO; // Default value

    @Column(name = "current_player_index")
    private Integer currentPlayerIndex; // Defaults to NULL

    @Column(name = "prev_player_index")
    private Integer prevPlayerIndex; // Defaults to NULL

    @Column(name = "next_player_index")
    private Integer nextPlayerIndex; // Defaults to NULL

    @Column(name = "total_joined_players", nullable = false)
    private Integer totalJoinedPlayers = 0; // Default value

    @Column(name = "total_playing_players", nullable = false)
    private Integer totalPlayingPlayers = 0; // Default value

    @Column(name = "total_waiting_players", nullable = false)
    private Integer totalWaitingPlayers = 0; // Default value

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime = LocalDateTime.now(); // Default value

    @Column(name = "end_time")
    private LocalDateTime endTime; // Defaults to NULL

    @Enumerated(EnumType.STRING)
    @Column(insertable=false, updatable=false)
    private GameStatus gameStatus = GameStatus.active; // Default value

    // Getters and Setters
    public BigDecimal getCurrentPot() {
        return currentPot;
    }

    public void setCurrentPot(BigDecimal currentPot) {
        this.currentPot = currentPot;
    }

    public GameTemplate getGameTemplate() {
        return gameTemplate;
    }

    public void setGameTemplate(GameTemplate gameTemplate) {
        this.gameTemplate = gameTemplate;
    }

    public BigDecimal getMaxBetLimit() {
        return maxBetLimit;
    }

    public void setMaxBetLimit(BigDecimal maxBetLimit) {
        this.maxBetLimit = maxBetLimit;
    }

    public BigDecimal getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(BigDecimal currentBet) {
        this.currentBet = currentBet;
    }

    public Integer getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(Integer currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Integer getPrevPlayerIndex() {
        return prevPlayerIndex;
    }

    public void setPrevPlayerIndex(Integer prevPlayerIndex) {
        this.prevPlayerIndex = prevPlayerIndex;
    }

    public Integer getNextPlayerIndex() {
        return nextPlayerIndex;
    }

    public void setNextPlayerIndex(Integer nextPlayerIndex) {
        this.nextPlayerIndex = nextPlayerIndex;
    }

    public Integer getTotalJoinedPlayers() {
        return totalJoinedPlayers;
    }

    public void setTotalJoinedPlayers(Integer totalJoinedPlayers) {
        this.totalJoinedPlayers = totalJoinedPlayers;
    }

    public Integer getTotalPlayingPlayers() {
        return totalPlayingPlayers;
    }

    public void setTotalPlayingPlayers(Integer totalPlayingPlayers) {
        this.totalPlayingPlayers = totalPlayingPlayers;
    }

    public Integer getTotalWaitingPlayers() {
        return totalWaitingPlayers;
    }

    public void setTotalWaitingPlayers(Integer totalWaitingPlayers) {
        this.totalWaitingPlayers = totalWaitingPlayers;
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

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    // Enum for Game Status
    public enum GameStatus {
        active, waitingForPlayer, completed
    }
}
