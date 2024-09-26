package com.auth.AuthImpl.ctp.entity;


import com.auth.AuthImpl.registraion.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(name = "player")
public class Player {

    @Id
    private Long payerId;

    @OneToOne
    @MapsId // This indicates that the primary key of Player is the same as userId
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false) // Reference to the userId from Users
    private Users user;


    @Column(nullable = false)
    private Long chips = 1000L;

    @Column(nullable = false)
    private String playerName;

    private String playerRank;

    // Constructors, Getters, and Setters
    public Player() {}

    public Player(Users user, Long chips, String playerName, String playerRank) {
        this.user = user;
        this.chips = chips;
        this.playerName = user.getUsername();
        this.playerRank = playerRank;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
        this.playerName = user.getUsername(); // Update playerName whenever user is set
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

