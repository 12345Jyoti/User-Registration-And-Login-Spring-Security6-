package com.auth.AuthImpl.ctp.util;


import com.auth.AuthImpl.ctp.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> deal(int numberOfCards) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            if (!cards.isEmpty()) {
                dealtCards.add(cards.remove(0));
            }
        }
        return dealtCards;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}

