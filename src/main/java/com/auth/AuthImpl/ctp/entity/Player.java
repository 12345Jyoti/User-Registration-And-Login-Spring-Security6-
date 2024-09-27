package com.auth.AuthImpl.ctp.entity;

import com.auth.AuthImpl.registraion.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(name = "player")
public class Player {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generate playerId
    private Long playerId;

    @OneToOne
    @MapsId // Indicates that the primary key of Player is the same as userId
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) // Reference to the userId from Users
    private Users user;

    @Column(nullable = false)
    private Long chips = 1000L; // Default chips for a new player

    @Column(nullable = false)
    private String playerName;

    private String playerRank;

    // Default constructor
    public Player() {}

    // Constructor with parameters
    public Player(Users user, Long chips, String playerRank) {
        this.user = user;
        this.chips = chips != null ? chips : 1000L; // Default to 1000 if chips are not provided
        this.playerName = user.getUsername(); // Set playerName from user
        this.playerRank = playerRank;
    }

    // Getters and Setters
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
        if (user != null) {
            this.playerName = user.getUsername(); // Update playerName whenever user is set
        }
    }

    public Long getChips() {
        return chips;
    }

    public void setChips(Long chips) {
        this.chips = chips;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerRank() {
        return playerRank;
    }

    public void setPlayerRank(String playerRank) {
        this.playerRank = playerRank;
    }
}
