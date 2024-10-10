package com.auth.AuthImpl.ctp.util;

import com.auth.AuthImpl.ctp.model.Card;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class CardDeserializer extends JsonDeserializer<Card> {

    @Override
    public Card deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String cardString = jp.getText(); // e.g., "J of Spades"
        String[] parts = cardString.split(" of ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid card format: " + cardString);
        }
        String rank = parts[0].trim();
        String suit = parts[1].trim();
        return new Card(rank, suit); // Create a new Card instance with rank and suit
    }
}
