package com.auth.AuthImpl.ctp.model;


public class Card {
    private String suit; // e.g., "Hearts", "Diamonds"
    private String rank; // e.g., "2", "3", "K", "A"

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

