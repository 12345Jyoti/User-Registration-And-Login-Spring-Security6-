package com.auth.AuthImpl.ctp.model;

import com.auth.AuthImpl.ctp.util.CardDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CardDeserializer.class)
public class Card {
    private String suit; // e.g., "Hearts", "Diamonds"
    private String rank; // e.g., "2", "3", "K", "A"

    // Default constructor required by Jackson for deserialization
    public Card(){}
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    // Convert rank to a comparable integer value
    public int getRankValue() {
        switch (rank) {
            case "2": return 2;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 7;
            case "8": return 8;
            case "9": return 9;
            case "10": return 10;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            case "A": return 14;
            default: return 0; // Invalid rank
        }
    }
}
