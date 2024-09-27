package com.auth.AuthImpl.ctp.entity;

import com.auth.AuthImpl.ctp.enums.GameResult;
import com.auth.AuthImpl.registraion.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(name = "player_game")
@IdClass(PlayerGameId.class)
public class PlayerGame {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    // Relationship with Users entity (Many PlayerGames to One User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    // Relationship with Game entity (Many PlayerGames to One Game)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", referencedColumnName = "gameId", insertable = false, updatable = false)
    private Game game;

    // Stores the player's bet amount for the game
    @Column(name = "bet_amount", nullable = false)
    private Long betAmount = 0L;

    // Stores the game result (WIN, LOSE, PENDING)
    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private GameResult result = GameResult.PENDING;

    // Store the player's hand as a comma-separated string of cards
    @Column(name = "cards")
    private String cards;

    // Track whether the player has folded during the game
    @Column(name = "has_folded", nullable = false)
    private boolean hasFolded = false;

    // Track whether the player has checked (i.e., not raised the bet)
    @Column(name = "is_currentPlayer", nullable = false)
    private boolean isCurrentPlayer ;

    // Track whether the player is playing blind (without seeing cards)
    @Column(name = "is_blind", nullable = false)
    private boolean isBlind = true; // Default to true, assuming players start blind

    // Constructor (default)
    public PlayerGame() {}

    // Parametrized constructor for easier initialization
    public PlayerGame(Long userId, Long gameId, Users user, Game game, Long betAmount, GameResult result,boolean isCurrentPlayer, boolean isBlind) {
        this.userId = userId;
        this.gameId = gameId;
        this.user = user;
        this.game = game;
        this.betAmount = betAmount;
        this.result = result;
        this.isCurrentPlayer=isCurrentPlayer;
        this.isBlind = isBlind;
    }

    // Getters and Setters
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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Long betAmount) {
        this.betAmount = betAmount;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public boolean getHasFolded() {
        return hasFolded;
    }

    public void setHasFolded(boolean hasFolded) {
        this.hasFolded = hasFolded;
    }

    public boolean getHasChecked() {
        return isCurrentPlayer;
    }

    public void setHasChecked(boolean isCurrentPlayer) {
        this.isCurrentPlayer = isCurrentPlayer;
    }

    public boolean getIsBlind() {
        return isBlind;
    }

    public void setIsBlind(boolean isBlind) {
        this.isBlind = isBlind;
    }
}
