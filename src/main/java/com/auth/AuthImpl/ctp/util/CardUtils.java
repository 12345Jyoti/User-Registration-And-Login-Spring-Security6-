package com.auth.AuthImpl.ctp.util;

import com.auth.AuthImpl.ctp.model.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class CardUtils {

    public static List<Card> convertStringToHand(List<String> cardStrings) {
        List<Card> hand = new ArrayList<>();
        for (String cardString : cardStrings) {
            String[] parts = cardString.split(" of ");
            if (parts.length == 2) {
                String rank = parts[0].trim();
                String suit = parts[1].trim();
                hand.add(new Card(rank, suit));
            }
        }
        return hand;
    }

    // Compare two hands of cards
    public static int compareHands(List<Card> hand1, List<Card> hand2) {
        int hand1Strength = calculateHandStrength(hand1);
        int hand2Strength = calculateHandStrength(hand2);

        return Integer.compare(hand1Strength, hand2Strength);
    }

    // Example method to calculate hand strength
    public static int calculateHandStrength(List<Card> hand) {
        int strength = 0;
        for (Card card : hand) {
            strength += card.getRankValue(); // Sum up the values for this example
        }
        return strength;
    }


////
////    public static int calculateHandStrength(List<Card> cards) {
////        int strength = 0;
////
////        // Example logic for hand strength
////        for (Card card : cards) {
////            switch (card.getRank()) {
////                case "A": strength += 14; break; // Ace
////                case "K": strength += 13; break; // King
////                case "Q": strength += 12; break; // Queen
////                case "J": strength += 11; break; // Jack
////                case "10": strength += 10; break; // Ten
////                default: strength += Integer.parseInt(card.getRank()); // Numeric cards
////            }
////        }
////        return strength;
////    }
//
//    public static int calculateHandStrength(List<Card> cards) {
//        int strength = 0;
//
//        for (Card card : cards) {
//            // Debugging output to see rank and suit
//            System.out.println("Card: " + card);
//
//            switch (card.getRank()) {  // Switch on rank, not suit
//                case "A": strength += 14; break; // Ace
//                case "K": strength += 13; break; // King
//                case "Q": strength += 12; break; // Queen
//                case "J": strength += 11; break; // Jack
//                case "10": strength += 10; break; // Ten
//                default:
//                    try {
//                        // Handle numeric cards from 2-9
//                        strength += Integer.parseInt(card.getRank()); // Numeric cards
//                    } catch (NumberFormatException e) {
//                        throw new IllegalArgumentException("Invalid card rank: " + card.getRank());
//                    }
//            }
//        }
//        return strength;
//    }
//
//
//
//
//
//    public static List<Card> convertStringToHand(List<String> cardStrings) {
//        List<Card> hand = new ArrayList<>();
//
//        for (String cardString : cardStrings) {
//            // Debugging output to see the original string
//            System.out.println("Parsing card string: " + cardString);
//
//            // Clean up the card string: Remove brackets and quotes
//            cardString = cardString.replaceAll("[\\[\\]\"]", "").trim();
//
//            // Split card string into rank and suit
//            String[] parts = cardString.split(" of ");
//            if (parts.length == 2) {
//                String rank = parts[0].trim();
//                String suit = parts[1].trim();
//                System.out.println("Parsed rank: " + rank + ", suit: " + suit);
//
//                if (!isValidSuit(suit)) {
//                    throw new IllegalArgumentException("Invalid card suit: " + suit);
//                }
//
//                // Add the new Card to the hand (ensure rank and suit are passed in the correct order)
//                hand.add(new Card(rank, suit));
//            } else {
//                throw new IllegalArgumentException("Invalid card format: " + cardString);
//            }
//        }
//
//        return hand;
//    }
//
//    private static boolean isValidSuit(String suit) {
//        // Check if the suit is one of the four valid suits
//        return suit.equals("Hearts") || suit.equals("Diamonds") || suit.equals("Clubs") || suit.equals("Spades");
//    }


//
//    public static List<Card> convertStringToHand(List<String> cardStrings) {
//            List<Card> hand = new ArrayList<>();
//
//            for (String cardString : cardStrings) {
//                Card card = parseCard(cardString);
//                if (card != null) {
//                    hand.add(card);
//                } else {
//                    throw new IllegalArgumentException("Invalid card format: " + cardString);
//                }
//            }
//
//            return hand;
//        }

        private static Card parseCard(String cardString) {
            if (cardString.length() < 2) {
                return null; // Invalid card string
            }

            String rankString = cardString.substring(0, cardString.length() - 1); // All but last char for rank
            String suitString = cardString.substring(cardString.length() - 1); // Last char for suit

            String suit;
            switch (suitString.toUpperCase()) {
                case "H":
                    suit = "Hearts";
                    break;
                case "D":
                    suit = "Diamonds";
                    break;
                case "C":
                    suit = "Clubs";
                    break;
                case "S":
                    suit = "Spades";
                    break;
                default:
                    return null; // Invalid suit
            }

            return new Card(suit, rankString); // Return a new Card object
        }


//    private static final Map<String, Integer> RANK_VALUES;
//
//    static {
//        RANK_VALUES = new HashMap<>();
//        RANK_VALUES.put("2", 2);
//        RANK_VALUES.put("3", 3);
//        RANK_VALUES.put("4", 4);
//        RANK_VALUES.put("5", 5);
//        RANK_VALUES.put("6", 6);
//        RANK_VALUES.put("7", 7);
//        RANK_VALUES.put("8", 8);
//        RANK_VALUES.put("9", 9);
//        RANK_VALUES.put("10", 10);
//        RANK_VALUES.put("J", 11);
//        RANK_VALUES.put("Q", 12);
//        RANK_VALUES.put("K", 13);
//        RANK_VALUES.put("A", 14);
//    }
//
//    // Card class to represent individual cards
//    public static class Card {
//        private String rank;
//        private String suit;
//
//        // Constructor for creating Card instances
//        public Card(String rank, String suit) {
//            this.rank = rank;
//            this.suit = suit;
//        }
//
//        public String getRank() {
//            return rank;
//        }
//
//        public void setRank(String rank) {
//            this.rank = rank;
//        }
//
//        public String getSuit() {
//            return suit;
//        }
//
//        public void setSuit(String suit) {
//            this.suit = suit;
//        }
//
//        @Override
//        public String toString() {
//            return rank + " of " + suit;
//        }
//    }
//
//    // Method to parse card JSON into a list of Card objects
//    public static List<Card> parseCards(String cardsJson) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return Arrays.asList(mapper.readValue(cardsJson, Card[].class));
//        } catch (Exception e) {
//            throw new RuntimeException("Error parsing cards: " + e.getMessage());
//        }
//    }
//
//    // Method to calculate the strength of a player's hand
//    public static int calculateHandStrength(List<Card> cards) {
//        return cards.stream()
//                .map(card -> RANK_VALUES.get(card.getRank()))  // Map ranks to their values
//                .filter(Objects::nonNull)  // Filter out null values to avoid NPE
//                .reduce(0, Integer::sum);  // Sum up the ranks for simplicity
//    }
//
//    // Method to compare two hands (for side show and final card show)
//    public static int compareHands(List<Card> hand1, List<Card> hand2) {
//        int strength1 = calculateHandStrength(hand1);
//        int strength2 = calculateHandStrength(hand2);
//
//        return Integer.compare(strength1, strength2);
//    }
}
