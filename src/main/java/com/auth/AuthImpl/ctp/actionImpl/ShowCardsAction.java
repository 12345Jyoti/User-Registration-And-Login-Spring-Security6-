package com.auth.AuthImpl.ctp.actionImpl;

import com.auth.AuthImpl.ctp.entity.PlayerGame;
import com.auth.AuthImpl.ctp.enums.GameResult;
import com.auth.AuthImpl.ctp.model.Card;
import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
import com.auth.AuthImpl.ctp.service.HandEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShowCardsAction {

    @Autowired
    private PlayerGameRepository playerGameRepository;

    // Map to track side show requests
    private Map<Long, List<PlayerGame>> sideShowRequests = new HashMap<>();

    public List<String> showCards(Long gameId) {
        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
        List<String> visibleCards = new ArrayList<>();
        List<PlayerGame> activePlayers = new ArrayList<>(); // To hold players who haven't folded

        for (PlayerGame playerGame : playersInGame) {
            if (!playerGame.getHasFolded()) {
                visibleCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
                activePlayers.add(playerGame); // Collect active players
            }
        }

        if (activePlayers.isEmpty()) {
            updatePlayerGameResults(playersInGame, Collections.emptyList()); // Handle folds
            return visibleCards;
        }

        // If only two active players, determine the winner directly
        if (activePlayers.size() == 2) {
            GameResult gameResult = determineWinner(activePlayers);
            updatePlayerGameResults(playersInGame, activePlayers); // Update results for all players
            return visibleCards;
        }

        // For more than two active players, handle the side show
        handleSideShow(activePlayers);

        return visibleCards; // Return visible cards for the player’s view
    }

    private void handleSideShow(List<PlayerGame> activePlayers) {
        // Gather side show requests from active players
        gatherSideShowRequests(activePlayers);

        // If there are not enough participants for a side show
        if (sideShowRequests.isEmpty()) {
            System.out.println("No side show requests received.");
            return;
        }

        // Handle each side show request
        for (Map.Entry<Long, List<PlayerGame>> entry : sideShowRequests.entrySet()) {
            List<PlayerGame> participants = entry.getValue();
            if (participants.size() < 2) {
                System.out.println("Not enough players for a side show.");
                continue;
            }

            // Show cards only to the side show participants
            List<String> sideShowCards = new ArrayList<>();
            for (PlayerGame playerGame : participants) {
                sideShowCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
            }

            // Announce the side show cards
            System.out.println("Side show participants:");
            for (String cardInfo : sideShowCards) {
                System.out.println(cardInfo);
            }

            // Determine the winner of the side show
            GameResult sideShowResult = determineWinner(participants);
            updatePlayerGameResults(activePlayers, participants); // Update results for all players
        }

        // Clear side show requests after processing
        sideShowRequests.clear();
    }

    private void gatherSideShowRequests(List<PlayerGame> activePlayers) {
        for (int i = 1; i < activePlayers.size(); i++) {
            PlayerGame requestingPlayer = activePlayers.get(i);
            PlayerGame previousPlayer = activePlayers.get(i - 1);

            // Simulate requesting a side show (could replace with actual user interaction)
            if (!requestingPlayer.getHasFolded() && new Random().nextBoolean()) {
                System.out.println("Player " + requestingPlayer.getUserId() + " has requested a side show with Player " + previousPlayer.getUserId());

                // Ask the previous player if they accept the side show request
                boolean accepted = askForSideShowAcceptance(previousPlayer);
                if (accepted) {
                    System.out.println("Player " + previousPlayer.getUserId() + " accepted the side show request.");
                    handleSideShowResult(requestingPlayer, previousPlayer);
                } else {
                    System.out.println("Player " + previousPlayer.getUserId() + " denied the side show request.");
                }
            }
        }
    }

    // Method to simulate asking the previous player if they accept the side show
    private boolean askForSideShowAcceptance(PlayerGame previousPlayer) {
        // Simulating acceptance logic; replace with actual user input in a real system
        return new Random().nextBoolean(); // Randomly accept or deny for simulation purposes
    }

    // Method to handle the result of the side show comparison
    private void handleSideShowResult(PlayerGame requestingPlayer, PlayerGame previousPlayer) {
        // Compare hands
        List<Card> hand1 = convertStringToHand(Arrays.asList(requestingPlayer.getCards().split(",")));
        List<Card> hand2 = convertStringToHand(Arrays.asList(previousPlayer.getCards().split(",")));

        int comparisonResult = HandEvaluator.compareHands(HandEvaluator.evaluateHand(hand1), HandEvaluator.evaluateHand(hand2));

        if (comparisonResult > 0) {
            // Requesting player wins, opponent folds
            System.out.println("Player " + previousPlayer.getUserId() + " loses side show and folds.");
            previousPlayer.setHasFolded(true);
        } else {
            // Requesting player loses and folds
            System.out.println("Player " + requestingPlayer.getUserId() + " loses side show and folds.");
            requestingPlayer.setHasFolded(true);
        }

        // Update the game state
        playerGameRepository.save(requestingPlayer);
        playerGameRepository.save(previousPlayer);
    }


    private GameResult determineWinner(List<PlayerGame> activePlayers) {
        if (activePlayers.isEmpty()) {
            return GameResult.PENDING; // No players left to decide
        }

        if (activePlayers.size() == 1) {
            // If there's only one active player, they automatically win
            PlayerGame winner = activePlayers.get(0);
            announceWinners(Collections.singletonList(winner)); // Announce the single winner
            return GameResult.WON; // There is a clear winner
        }

        // Handle the case where there are exactly 2 active players
        if (activePlayers.size() == 2) {
            PlayerGame player1 = activePlayers.get(0);
            PlayerGame player2 = activePlayers.get(1);

            List<Card> hand1 = convertStringToHand(Arrays.asList(player1.getCards().split(",")));
            List<Card> hand2 = convertStringToHand(Arrays.asList(player2.getCards().split(",")));

            String handType1 = HandEvaluator.evaluateHand(hand1); // Assuming HandEvaluator exists
            String handType2 = HandEvaluator.evaluateHand(hand2);

            // Compare the hands
            int comparisonResult = HandEvaluator.compareHands(handType1, handType2);

            if (comparisonResult > 0) {
                // Player 1 wins
                player1.setResult(GameResult.WON);
                player2.setResult(GameResult.LOST);
                announceWinners(Collections.singletonList(player1));
                return GameResult.WON; // Player 1 is the winner
            } else if (comparisonResult < 0) {
                // Player 2 wins
                player1.setResult(GameResult.LOST);
                player2.setResult(GameResult.WON);
                announceWinners(Collections.singletonList(player2));
                return GameResult.WON; // Player 2 is the winner
            } else {
                // It's a tie, handle accordingly
                System.out.println("No clear winner due to a tie between Player "
                        + player1.getUserId() + " and Player "
                        + player2.getUserId());
                return GameResult.PENDING; // No clear winner
            }
        }

        // If there are more than two active players, handle as before
        Map<String, String> playerResults = new HashMap<>();
        String bestHand = null;
        PlayerGame singleWinner = null;

        // Evaluate hands for active players
        for (PlayerGame playerGame : activePlayers) {
            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
            String handType = HandEvaluator.evaluateHand(hand);
            playerResults.put(playerGame.getUserId().toString(), handType);
        }

        // Find the best hand
        for (PlayerGame playerGame : activePlayers) {
            String currentHandType = playerResults.get(playerGame.getUserId().toString());

            if (bestHand == null || HandEvaluator.compareHands(currentHandType, bestHand) > 0) {
                bestHand = currentHandType;
                singleWinner = playerGame; // Set this player as the current best player
            } else if (HandEvaluator.compareHands(currentHandType, bestHand) == 0) {
                // If there's a tie, set singleWinner to null to indicate no clear winner
                singleWinner = null;
            }
        }

        // Announce the winner
        if (singleWinner != null) {
            announceWinners(Collections.singletonList(singleWinner)); // Announce the single winner
            return GameResult.WON; // There is a clear winner
        } else {
            System.out.println("No clear winner due to a tie.");
            return GameResult.PENDING; // No clear winner
        }
    }

    private void updatePlayerGameResults(List<PlayerGame> playersInGame, List<PlayerGame> activePlayers) {
        // Determine winners among active players
        List<PlayerGame> winners = determineWinners(activePlayers);

        for (PlayerGame playerGame : playersInGame) {
            if (playerGame.getHasFolded()) {
                playerGame.setResult(GameResult.LOST); // If player has folded, they lost
            } else if (winners.contains(playerGame)) {
                playerGame.setResult(GameResult.WON); // If player is a winner
            } else {
                playerGame.setResult(GameResult.LOST); // If player is a loser
            }
            playerGameRepository.save(playerGame); // Save the updated player game state
        }
    }

    private List<PlayerGame> determineWinners(List<PlayerGame> activePlayers) {
        if (activePlayers.isEmpty()) {
            return Collections.emptyList(); // No winners if there are no active players
        }

        Map<String, String> playerResults = new HashMap<>();
        PlayerGame bestPlayer = null;
        String bestHand = null;

        // Evaluate hands for active players and determine the best hand
        for (PlayerGame playerGame : activePlayers) {
            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
            String handType = HandEvaluator.evaluateHand(hand); // Assuming HandEvaluator exists

            // Determine if this hand is the best
            if (bestHand == null || HandEvaluator.compareHands(handType, bestHand) > 0) {
                bestHand = handType;
                bestPlayer = playerGame;
            }
        }

        // Return the best player as the winner
        return bestPlayer != null ? Collections.singletonList(bestPlayer) : Collections.emptyList();
    }

    private void announceWinners(List<PlayerGame> winners) {
        if (winners.isEmpty()) {
            System.out.println("No winners this round.");
        } else {
            StringBuilder winnerMessage = new StringBuilder("Winner: ");
            for (PlayerGame winner : winners) {
                winnerMessage.append("Player ").append(winner.getUserId()).append(", ");
            }
            // Remove last comma and space
            winnerMessage.setLength(winnerMessage.length() - 2);
            System.out.println(winnerMessage.toString());
        }
    }

    private List<Card> convertStringToHand(List<String> handString) {
        List<Card> hand = new ArrayList<>();
        for (String cardString : handString) {
            String[] parts = cardString.split(" of ");
            String rank = parts[0].trim();
            String suit = parts[1].trim();
            hand.add(new Card(suit, rank)); // Card object takes suit and rank
        }
        return hand;
    }
}


//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameResult;
//import com.auth.AuthImpl.ctp.model.Card;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.service.HandEvaluator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class ShowCardsAction {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    // Map to track side show requests
//    private Map<Long, List<PlayerGame>> sideShowRequests = new HashMap<>();
//
//    public List<String> showCards(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        List<String> visibleCards = new ArrayList<>();
//        List<PlayerGame> activePlayers = new ArrayList<>(); // To hold players who haven't folded
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (!playerGame.getHasFolded()) {
//                visibleCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
//                activePlayers.add(playerGame); // Collect active players
//            }
//        }
//
//        if (activePlayers.isEmpty()) {
//            updatePlayerGameResults(playersInGame, Collections.emptyList()); // Handle folds
//            return visibleCards;
//        }
//
//        // If only two active players, determine the winner directly
//        if (activePlayers.size() == 2) {
//            GameResult gameResult = determineWinner(activePlayers);
//            updatePlayerGameResults(playersInGame, activePlayers); // Update results for all players
//            return visibleCards;
//        }
//
//        // For more than two active players, handle the side show
//        handleSideShow(activePlayers);
//
//        return visibleCards; // Return visible cards for the player’s view
//    }
//
//    private void handleSideShow(List<PlayerGame> activePlayers) {
//        // Gather side show requests from active players
//        gatherSideShowRequests(activePlayers);
//
//        // If there are not enough participants for a side show
//        if (sideShowRequests.isEmpty()) {
//            System.out.println("No side show requests received.");
//            return;
//        }
//
//        // Handle each side show request
//        for (Map.Entry<Long, List<PlayerGame>> entry : sideShowRequests.entrySet()) {
//            List<PlayerGame> participants = entry.getValue();
//            if (participants.size() < 2) {
//                System.out.println("Not enough players for a side show.");
//                continue;
//            }
//
//            // Show cards only to the side show participants
//            List<String> sideShowCards = new ArrayList<>();
//            for (PlayerGame playerGame : participants) {
//                sideShowCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
//            }
//
//            // Announce the side show cards
//            System.out.println("Side show participants:");
//            for (String cardInfo : sideShowCards) {
//                System.out.println(cardInfo);
//            }
//
//            // Determine the winner of the side show
//            GameResult sideShowResult = determineWinner(participants);
//            updatePlayerGameResults(activePlayers, participants); // Update results for all players
//        }
//
//        // Clear side show requests after processing
//        sideShowRequests.clear();
//    }
//
//    private void gatherSideShowRequests(List<PlayerGame> activePlayers) {
//        for (int i = 1; i < activePlayers.size(); i++) {
//            PlayerGame requestingPlayer = activePlayers.get(i);
//            PlayerGame previousPlayer = activePlayers.get(i - 1);
//
//            // Simulate requesting a side show (could replace with actual user interaction)
//            if (!requestingPlayer.getHasFolded() && new Random().nextBoolean()) {
//                System.out.println("Player " + requestingPlayer.getUserId() + " has requested a side show with Player " + previousPlayer.getUserId());
//
//                // Ask the previous player if they accept the side show request
//                boolean accepted = askForSideShowAcceptance(previousPlayer);
//                if (accepted) {
//                    System.out.println("Player " + previousPlayer.getUserId() + " accepted the side show request.");
//                    handleSideShowResult(requestingPlayer, previousPlayer);
//                } else {
//                    System.out.println("Player " + previousPlayer.getUserId() + " denied the side show request.");
//                }
//            }
//        }
//    }
//
//    // Method to simulate asking the previous player if they accept the side show
//    private boolean askForSideShowAcceptance(PlayerGame previousPlayer) {
//        // Simulating acceptance logic; replace with actual user input in a real system
//        return new Random().nextBoolean(); // Randomly accept or deny for simulation purposes
//    }
//
//    // Method to handle the result of the side show comparison
//    private void handleSideShowResult(PlayerGame requestingPlayer, PlayerGame previousPlayer) {
//        // Compare hands
//        List<Card> hand1 = convertStringToHand(Arrays.asList(requestingPlayer.getCards().split(",")));
//        List<Card> hand2 = convertStringToHand(Arrays.asList(previousPlayer.getCards().split(",")));
//
//        int comparisonResult = HandEvaluator.compareHands(HandEvaluator.evaluateHand(hand1), HandEvaluator.evaluateHand(hand2));
//
//        if (comparisonResult > 0) {
//            // Requesting player wins, opponent folds
//            System.out.println("Player " + previousPlayer.getUserId() + " loses side show and folds.");
//            previousPlayer.setHasFolded(true);
//        } else {
//            // Requesting player loses and folds
//            System.out.println("Player " + requestingPlayer.getUserId() + " loses side show and folds.");
//            requestingPlayer.setHasFolded(true);
//        }
//
//        // Update the game state
//        playerGameRepository.save(requestingPlayer);
//        playerGameRepository.save(previousPlayer);
//    }
//
//    private GameResult determineWinner(List<PlayerGame> activePlayers) {
//        if (activePlayers.isEmpty()) {
//            return GameResult.PENDING; // No players left to decide
//        }
//
//        if (activePlayers.size() == 1) {
//            // If there's only one active player, they automatically win
//            PlayerGame winner = activePlayers.get(0);
//            announceWinners(Collections.singletonList(winner)); // Announce the single winner
//            return GameResult.WON; // There is a clear winner
//        }
//
//        if (activePlayers.size() == 2) {
//            PlayerGame player1 = activePlayers.get(0);
//            PlayerGame player2 = activePlayers.get(1);
//
//            List<Card> hand1 = convertStringToHand(Arrays.asList(player1.getCards().split(",")));
//            List<Card> hand2 = convertStringToHand(Arrays.asList(player2.getCards().split(",")));
//
//            String handType1 = HandEvaluator.evaluateHand(hand1);
//            String handType2 = HandEvaluator.evaluateHand(hand2);
//
//            int comparisonResult = HandEvaluator.compareHands(handType1, handType2);
//
//            if (comparisonResult > 0) {
//                player1.setResult(GameResult.WON);
//                player2.setResult(GameResult.LOST);
//                announceWinners(Collections.singletonList(player1));
//                return GameResult.WON; // Player 1 is the winner
//            } else if (comparisonResult < 0) {
//                player1.setResult(GameResult.LOST);
//                player2.setResult(GameResult.WON);
//                announceWinners(Collections.singletonList(player2));
//                return GameResult.WON; // Player 2 is the winner
//            } else {
//                System.out.println("No clear winner due to a tie between Player "
//                        + player1.getUserId() + " and Player "
//                        + player2.getUserId());
//                return GameResult.PENDING; // No clear winner
//            }
//        }
//
//        Map<String, String> playerResults = new HashMap<>();
//        String bestHand = null;
//        PlayerGame singleWinner = null;
//
//        for (PlayerGame playerGame : activePlayers) {
//            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
//            String handType = HandEvaluator.evaluateHand(hand);
//            playerResults.put(playerGame.getUserId().toString(), handType);
//        }
//
//        for (PlayerGame playerGame : activePlayers) {
//            String currentHandType = playerResults.get(playerGame.getUserId().toString());
//
//            if (bestHand == null || HandEvaluator.compareHands(currentHandType, bestHand) > 0) {
//                bestHand = currentHandType;
//                singleWinner = playerGame;
//            } else if (HandEvaluator.compareHands(currentHandType, bestHand) == 0) {
//                singleWinner = null;
//            }
//        }
//
//        if (singleWinner != null) {
//            announceWinners(Collections.singletonList(singleWinner));
//            return GameResult.WON;
//        } else {
//            System.out.println("No clear winner due to a tie.");
//            return GameResult.PENDING;
//        }
//    }
//
//    private void updatePlayerGameResults(List<PlayerGame> playersInGame, List<PlayerGame> activePlayers) {
//        List<PlayerGame> winners = determineWinners(activePlayers);
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (playerGame.getHasFolded()) {
//                playerGame.setResult(GameResult.LOST);
//            } else if (winners.contains(playerGame)) {
//                playerGame.setResult(GameResult.WON);
//            } else {
//                playerGame.setResult(GameResult.LOST);
//            }
//            playerGameRepository.save(playerGame);
//        }
//    }
//
//    private List<PlayerGame> determineWinners(List<PlayerGame> activePlayers) {
//        if (activePlayers.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<PlayerGame> winners = new ArrayList<>();
//
//        for (PlayerGame player : activePlayers) {
//            winners.add(player);
//        }
//        return winners;
//    }
//
//    private List<Card> convertStringToHand(List<String> cardStrings) {
//        List<Card> hand = new ArrayList<>();
//        for (String cardStr : cardStrings) {
//            hand.add(new Card(cardStr)); // Assuming the Card constructor takes a string representation
//        }
//        return hand;
//    }
//
//    private void announceWinners(List<PlayerGame> winners) {
//        System.out.println("Winner(s):");
//        for (PlayerGame winner : winners) {
//            System.out.println("Player " + winner.getUserId() + " is the winner.");
//        }
//    }
//}
//
