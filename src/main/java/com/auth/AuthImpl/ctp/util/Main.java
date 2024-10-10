package com.auth.AuthImpl.ctp.util;
//import java.util.*;
//
//public class Main {
//    public static void main(String[] args) {
//        List<Card> player1Cards = Arrays.asList(
//                new Card("K", "Diamonds"),
//                new Card("10", "Clubs"),
//                new Card("Q", "Spades")
//        );
//
//        List<Card> player2Cards = Arrays.asList(
//                new Card("5", "Hearts"),
//                new Card("7", "Diamonds"),
//                new Card("10", "Hearts")
//        );
//
//        String result = determineWinner(player1Cards, player2Cards);
//        System.out.println(result);
//    }
//
//    private static String determineWinner(List<Card> player1Cards, List<Card> player2Cards) {
//        int player1Strength = calculateHandStrength(player1Cards);
//        int player2Strength = calculateHandStrength(player2Cards);
//
//        if (player1Strength > player2Strength) {
//            return "Player 1 wins!";
//        } else if (player2Strength > player1Strength) {
//            return "Player 2 wins!";
//        } else {
//            // Handle tie
//            return handleTie(player1Cards, player2Cards);
//        }
//    }
//
//    private static int calculateHandStrength(List<Card> cards) {
//        int strength = 0;
//
//        for (Card card : cards) {
//            switch (card.getRank()) {
//                case "A":
//                    strength += 14; break; // Ace
//                case "K":
//                    strength += 13; break; // King
//                case "Q":
//                    strength += 12; break; // Queen
//                case "J":
//                    strength += 11; break; // Jack
//                case "10":
//                    strength += 10; break; // Ten
//                default:
//                    strength += Integer.parseInt(card.getRank()); // Numeric cards
//            }
//        }
//        return strength;
//    }
//
//    private static String handleTie(List<Card> player1Cards, List<Card> player2Cards) {
//        List<Integer> player1Ranks = getSortedRanks(player1Cards);
//        List<Integer> player2Ranks = getSortedRanks(player2Cards);
//
//        for (int i = 0; i < Math.min(player1Ranks.size(), player2Ranks.size()); i++) {
//            if (player1Ranks.get(i) > player2Ranks.get(i)) {
//                return "Player 1 wins by highest card!";
//            } else if (player1Ranks.get(i) < player2Ranks.get(i)) {
//                return "Player 2 wins by highest card!";
//            }
//        }
//
//        return "It's a tie!"; // All cards are equal
//    }
//
//    private static List<Integer> getSortedRanks(List<Card> cards) {
//        List<Integer> ranks = new ArrayList<>();
//        for (Card card : cards) {
//            switch (card.getRank()) {
//                case "A":
//                    ranks.add(14);
//                    break;
//                case "K":
//                    ranks.add(13);
//                    break;
//                case "Q":
//                    ranks.add(12);
//                    break;
//                case "J":
//                    ranks.add(11);
//                    break;
//                case "10":
//                    ranks.add(10);
//                    break;
//                default:
//                    ranks.add(Integer.parseInt(card.getRank()));
//            }
//        }
//        Collections.sort(ranks, Collections.reverseOrder());
//        return ranks; // Returns sorted ranks in descending order
//    }
//
//    static class Card {
//        private String rank;
//        private String suit;
//
//        public Card(String rank, String suit) {
//            this.rank = rank;
//            this.suit = suit;
//        }
//
//        public String getRank() {
//            return rank;
//        }
//
//        public String getSuit() {
//            return suit;
//        }
//    }
//}

import com.auth.AuthImpl.ctp.model.Card;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> player1Cards = Arrays.asList("K of Diamonds", "10 of Clubs", "Q of Spades");
        List<String> player2Cards = Arrays.asList("5 of Hearts", "7 of Diamonds", "10 of Hearts");

        List<Card> hand1 = CardUtils.convertStringToHand(player1Cards);
        List<Card> hand2 = CardUtils.convertStringToHand(player2Cards);

        int comparison = CardUtils.compareHands(hand1, hand2);

        if (comparison > 0) {
            System.out.println("Player 1 wins!");
        } else if (comparison < 0) {
            System.out.println("Player 2 wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
