package com.auth.AuthImpl.ctp.service;

import com.auth.AuthImpl.ctp.model.Card;

import java.util.*;

public class HandEvaluator {

    private static final List<String> RANK_ORDER = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

    // Map hand rankings to values for easier comparison
    private static final Map<String, Integer> HAND_RANKINGS = new HashMap<>();
    static {
        HAND_RANKINGS.put("TRAIL", 6);
        HAND_RANKINGS.put("PURE SEQUENCE", 5);
        HAND_RANKINGS.put("SEQUENCE", 4);
        HAND_RANKINGS.put("COLOR", 3);
        HAND_RANKINGS.put("PAIR", 2);
        HAND_RANKINGS.put("HIGH CARD", 1);
    }

    public static String evaluateHand(List<Card> hand) {
        Map<String, Integer> rankCount = new HashMap<>();
        Map<String, List<Card>> suitMap = new HashMap<>();

        for (Card card : hand) {
            rankCount.put(card.getRank(), rankCount.getOrDefault(card.getRank(), 0) + 1);

            // Group cards by suit
            suitMap.putIfAbsent(card.getSuit(), new ArrayList<>());
            suitMap.get(card.getSuit()).add(card);
        }

        // Check for pairs, triples
        boolean hasPair = false;
        boolean hasTriple = false;

        for (int count : rankCount.values()) {
            if (count == 3) {
                hasTriple = true;
            } else if (count == 2) {
                hasPair = true;
            }
        }

        // Check for pure sequence and sequence
        if (hasTriple) {
            return "TRAIL";
        }

        boolean isFlush = suitMap.size() == 1; // All cards same suit
        boolean isStraight = isStraight(hand); // Check for a straight

        if (isFlush && isStraight) {
            return "PURE SEQUENCE";
        } else if (isStraight) {
            return "SEQUENCE";
        } else if (isFlush) {
            return "COLOR";
        } else if (hasPair) {
            return "PAIR";
        } else {
            return "HIGH CARD";
        }
    }

    // Method to compare two hands based on their evaluated rank
    public static int compareHands(String hand1Type, String hand2Type) {
        int hand1Rank = HAND_RANKINGS.getOrDefault(hand1Type, 0);
        int hand2Rank = HAND_RANKINGS.getOrDefault(hand2Type, 0);

        return Integer.compare(hand1Rank, hand2Rank);
    }

    // Check if the hand is a straight
    private static boolean isStraight(List<Card> hand) {
        List<Integer> ranks = new ArrayList<>();
        Map<String, Integer> rankValueMap = new HashMap<>();

        // Map ranks to their values based on RANK_ORDER
        for (int i = 0; i < RANK_ORDER.size(); i++) {
            rankValueMap.put(RANK_ORDER.get(i), i);
        }

        for (Card card : hand) {
            if (card == null || card.getRank() == null) {
                throw new IllegalArgumentException("Card or card rank cannot be null. Card: " + card);
            }

            // Clean the rank string to remove unwanted characters
            String rank = cleanRank(card.getRank());

            // Log the cleaned rank value before attempting to get it from the map
            System.out.println("Processing card with cleaned rank: " + rank);

            Integer rankValue = rankValueMap.get(rank);

            if (rankValue == null) {
                // Log the invalid rank before throwing the exception
                System.err.println("Invalid rank encountered: " + rank);
                throw new IllegalArgumentException("Invalid rank: " + rank);
            }

            ranks.add(rankValue);
        }

        Collections.sort(ranks);  // Sorting now safe because we checked for null

        // Check for consecutive ranks
        for (int i = 1; i < ranks.size(); i++) {
            if (ranks.get(i) - ranks.get(i - 1) != 1) {
                return false;
            }
        }
        return true;
    }

    // Helper function to clean the rank string
    private static String cleanRank(String rank) {
        // Remove unwanted characters and return a trimmed version of the rank
        return rank.replaceAll("[\\[\\]\"]", "").trim();
    }


}
