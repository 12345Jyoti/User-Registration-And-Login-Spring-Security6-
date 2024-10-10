package com.auth.AuthImpl.ctp.actionImpl;

import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.enums.GameCurrentStatus;
import com.auth.AuthImpl.ctp.enums.PlayerGameResult;
import com.auth.AuthImpl.ctp.model.Card;
import com.auth.AuthImpl.ctp.nenity.GameInstance;
import com.auth.AuthImpl.ctp.nenity.GamePlayer;
import com.auth.AuthImpl.ctp.nenity.GameResult;
import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
import com.auth.AuthImpl.ctp.repository.GamePlayerRepository;
import com.auth.AuthImpl.ctp.repository.GameResultRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import com.auth.AuthImpl.ctp.util.CardUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShowCardsAction {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameInstanceRepository gameInstanceRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<String> showCards(Long gameId) {
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
        List<String> visibleCards = new ArrayList<>();
        List<GamePlayer> activePlayers = filterActivePlayers(playersInGame, visibleCards);

        if (activePlayers.isEmpty()) {
            concludeGame(playersInGame, Collections.emptyList(), gameId);
            return visibleCards;
        }

        if (activePlayers.size() == 2) {
            GamePlayer winner = determineWinner(activePlayers, gameId);
            concludeGame(playersInGame, Collections.singletonList(winner), gameId);
            return visibleCards;
        }

        handleSideShow(activePlayers, gameId);
        return visibleCards;
    }

    private List<GamePlayer> filterActivePlayers(List<GamePlayer> playersInGame, List<String> visibleCards) {
        return playersInGame.stream()
                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
                .peek(player -> visibleCards.addAll(Arrays.asList(player.getCards().split(","))))
                .collect(Collectors.toList());
    }

    private GamePlayer determineWinner(List<GamePlayer> activePlayers, Long gameId) {
        if (activePlayers.isEmpty()) return null;

        GamePlayer winner = activePlayers.get(0);

        for (int i = 1; i < activePlayers.size(); i++) {
            GamePlayer currentPlayer = activePlayers.get(i);
            int currentStrength = evaluateHandStrength(currentPlayer.getCards());
            int winnerStrength = evaluateHandStrength(winner.getCards());

            if (currentStrength > winnerStrength) {
                winner = currentPlayer;
            } else if (currentStrength == winnerStrength) {
                winner = null; // Indicate a tie
            }
        }

        if (winner != null) {
            winner.setResult(PlayerGameResult.win);
            announceWinners(Collections.singletonList(winner));
            saveGameResult(gameId, winner, activePlayers);
        } else {
            System.out.println("No clear winner due to a tie among active players.");
        }

        return winner;
    }

    private void saveGameResult(Long gameId, GamePlayer winner, List<GamePlayer> activePlayers) {
        BigDecimal totalBettingAmount = activePlayers.stream()
                .map(GamePlayer::getPlayerBettingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        GameResult gameResult = new GameResult();
        gameResult.setGameId(gameId);
        gameResult.setWinningPlayerId(winner.getPlayerId().intValue());
        gameResult.setWinningAmount(totalBettingAmount);
        gameResult.setCreatedBy("ADMIN");
        gameResult.setCreatedAt(LocalDateTime.now());

        gameResultRepository.save(gameResult);
    }

    private void announceWinners(List<GamePlayer> winners) {
        if (winners == null || winners.isEmpty()) {
            System.out.println("No winners to announce.");
            return;
        }

        String message = "Winners: " + winners.stream()
                .map(winner -> "Player " + winner.getPlayerId() + " with cards: " + winner.getCards())
                .collect(Collectors.joining(", "));

        System.out.println(message);
        broadcastEvent("WINNERS_ANNOUNCED", message);
    }

    private int evaluateHandStrength(String cardsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Card> playerCards = Arrays.asList(objectMapper.readValue(cardsJson, Card[].class));
            if (playerCards.isEmpty()) throw new IllegalArgumentException("No valid cards found for evaluation.");
            return CardUtils.calculateHandStrength(playerCards);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid card format: " + e.getMessage());
            throw new IllegalStateException("Error evaluating hand strength: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while evaluating hand strength: " + e.getMessage());
            throw new IllegalStateException("An error occurred while evaluating hand strength.", e);
        }
    }

    private void concludeGame(List<GamePlayer> playersInGame, List<GamePlayer> winners, Long gameId) {
        updatePlayerGameResults(playersInGame, winners);
        markGameAsCompleted(gameId);
    }

    private void updatePlayerGameResults(List<GamePlayer> playersInGame, List<GamePlayer> winners) {
        for (GamePlayer player : playersInGame) {
            player.setResult(winners.contains(player) ? PlayerGameResult.win : PlayerGameResult.lose);
            gamePlayerRepository.save(player);
        }
    }

    private void markGameAsCompleted(Long gameId) {
        GameInstance gameInstance = gameInstanceRepository.findById(gameId).orElseThrow();
        gameInstance.setGameStatus(GameInstance.GameStatus.completed);
        gameInstanceRepository.save(gameInstance);
    }

    private void handleSideShow(List<GamePlayer> activePlayers, Long gameId) {
        broadcastEvent("SIDE_SHOW", gameId);
        // Additional logic for side show can be implemented here
    }

    protected void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }
}

//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.enums.GameCurrentStatus;
//import com.auth.AuthImpl.ctp.enums.PlayerGameResult;
//import com.auth.AuthImpl.ctp.model.Card;
//import com.auth.AuthImpl.ctp.nenity.GameInstance;
//import com.auth.AuthImpl.ctp.nenity.GamePlayer;
//import com.auth.AuthImpl.ctp.nenity.GameResult;
//import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
//import com.auth.AuthImpl.ctp.repository.GamePlayerRepository;
//import com.auth.AuthImpl.ctp.repository.GameResultRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.ctp.util.CardUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ShowCardsAction {
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private GameInstanceRepository gameInstanceRepository;
//
//    @Autowired
//    private GamePlayerRepository gamePlayerRepository;
//
//    @Autowired
//    private GameResultRepository gameResultRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    public List<String> showCards(Long gameId) {
//        // Fetch players in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//        List<String> visibleCards = new ArrayList<>();
//
//        // Filter out active players and collect their visible cards
//        List<GamePlayer> activePlayers = filterActivePlayers(playersInGame, visibleCards);
//
//        // If no active players are left, update results and mark the game as completed
//        if (activePlayers.isEmpty()) {
//            updatePlayerGameResults(playersInGame, Collections.emptyList());
//            markGameAsCompleted(gameId);
//            return visibleCards;
//        }
//
//        // Determine the winner if exactly two active players remain
//        if (activePlayers.size() == 2) {
//            GamePlayer winner = determineWinner(activePlayers,gameId);
//            updatePlayerGameResults(playersInGame, Collections.singletonList(winner));
//            markGameAsCompleted(gameId);
//            return visibleCards;
//        }
//
//        // Handle side show for more than two active players
//        handleSideShow(activePlayers, gameId);
//        return visibleCards;
//    }
//
//    private List<GamePlayer> filterActivePlayers(List<GamePlayer> playersInGame, List<String> visibleCards) {
//        return playersInGame.stream()
//                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
//                .peek(player -> visibleCards.addAll(Arrays.asList(player.getCards().split(",")))) // Collect visible cards
//                .collect(Collectors.toList());
//    }
//
//    private GamePlayer determineWinner(List<GamePlayer> activePlayers, Long gameId) {
//        if (activePlayers.isEmpty()) {
//            return null; // No players left to decide
//        }
//
//        // If there's only one active player, they automatically win
//        if (activePlayers.size() == 1) {
//            GamePlayer winner = activePlayers.get(0);
//            winner.setResult(PlayerGameResult.win);
//            announceWinners(Collections.singletonList(winner));
//            return winner;
//        }
//
//        GamePlayer winner = activePlayers.get(0); // Start with the first player as the initial winner
//
//        // Compare the hand strengths of all active players
//        for (int i = 1; i < activePlayers.size(); i++) {
//            GamePlayer currentPlayer = activePlayers.get(i);
//            int currentStrength = evaluateHandStrength(currentPlayer.getCards());
//            int winnerStrength = evaluateHandStrength(winner.getCards());
//
//            if (currentStrength > winnerStrength) {
//                winner = currentPlayer; // Update the winner
//            } else if (currentStrength == winnerStrength) {
//                winner = null; // Indicate a tie
//            }
//        }
//
//        // Announce the winner or tie case
//        if (winner != null) {
//            winner.setResult(PlayerGameResult.win);
//            announceWinners(Collections.singletonList(winner));
//            saveGameResult(gameId, winner,activePlayers); // Save the result if there is a clear winner
//        } else {
//            System.out.println("No clear winner due to a tie among active players.");
//        }
//
//        return winner; // Return the winning player (or null if no clear winner)
//    }
//
//    private void saveGameResult(Long gameId, GamePlayer winner, List<GamePlayer> activePlayers) {
//        // Calculate total betting amount from active players
//        BigDecimal totalBettingAmount = activePlayers.stream()
//                .map(GamePlayer::getPlayerBettingAmount) // Now returns BigDecimal
//                .reduce(BigDecimal.ZERO, BigDecimal::add); // Use BigDecimal::add for summation
//
//
//        GameResult gameResult = new GameResult();
//        gameResult.setGameId(gameId);
//        gameResult.setWinningPlayerId(winner.getPlayerId().intValue());
//        gameResult.setWinningAmount(totalBettingAmount); // Set total betting amount as winning amount
//        gameResult.setCreatedBy("ADMIN");
//        gameResult.setCreatedAt(LocalDateTime.now());
//
//        // Save the game result to the repository
//        gameResultRepository.save(gameResult);
//    }
//
//    private void announceWinners(List<GamePlayer> winners) {
//        if (winners == null || winners.isEmpty()) {
//            System.out.println("No winners to announce.");
//            return;
//        }
//
//        StringBuilder message = new StringBuilder("Winners: ");
//        winners.forEach(winner -> message.append("Player ")
//                .append(winner.getPlayerId())
//                .append(" with cards: ")
//                .append(winner.getCards())
//                .append(", "));
//
//        // Remove the last comma and space
//        if (message.length() > 2) {
//            message.setLength(message.length() - 2); // Remove last ", "
//        }
//
//        System.out.println(message.toString());
//        broadcastEvent("WINNERS_ANNOUNCED", message.toString());
//    }
//
//    private int evaluateHandStrength(String cardsJson) {
//        try {
//            // Convert JSON string to a list of Card objects
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<Card> playerCards = Arrays.asList(objectMapper.readValue(cardsJson, Card[].class));
//
//            // Check if we got valid cards
//            if (playerCards.isEmpty()) {
//                throw new IllegalArgumentException("No valid cards found for evaluation.");
//            }
//
//            return CardUtils.calculateHandStrength(playerCards);
//        } catch (IllegalArgumentException e) {
//            System.err.println("Invalid card format: " + e.getMessage());
//            throw new IllegalStateException("Error evaluating hand strength: " + e.getMessage(), e);
//        } catch (Exception e) {
//            System.err.println("An unexpected error occurred while evaluating hand strength: " + e.getMessage());
//            throw new IllegalStateException("An error occurred while evaluating hand strength.", e);
//        }
//    }
//
//
//    public static List<Card> convertStringToHand(List<String> cardStrings) {
//        List<Card> hand = new ArrayList<>();
//
//        for (String cardString : cardStrings) {
//            // Clean up the card string: Remove brackets and quotes
//            cardString = cardString.replaceAll("[\\[\\]\"]", "").trim();
//
//            // Split card string into rank and suit
//            String[] parts = cardString.split(" of ");
//            if (parts.length == 2) {
//                String rank = parts[0].trim();
//                String suit = parts[1].trim();
//
//                // Ensure the suit is valid before proceeding
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
//
//
//    private static void validateCard(String rank, String suit) {
//        List<String> validRanks = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace");
//        List<String> validSuits = Arrays.asList("Hearts", "Diamonds", "Clubs", "Spades");
//
//        if (!validRanks.contains(rank)) {
//            throw new IllegalArgumentException("Invalid rank: " + rank);
//        }
//        if (!validSuits.contains(suit)) {
//            throw new IllegalArgumentException("Invalid suit: " + suit);
//        }
//    }
//
//
//
//    private void updatePlayerGameResults(List<GamePlayer> playersInGame, List<GamePlayer> winners) {
//        // Implement logic to update player results based on the winners
//        for (GamePlayer player : playersInGame) {
//            if (winners.contains(player)) {
//                player.setResult(PlayerGameResult.win);
//            } else {
//                player.setResult(PlayerGameResult.lose);
//            }
//            gamePlayerRepository.save(player);
//        }
//    }
//
//    private void markGameAsCompleted(Long gameId) {
//        // Logic to mark the game as completed
//        GameInstance gameInstance = gameInstanceRepository.findById(gameId).orElseThrow();
//        gameInstance.setGameStatus(GameInstance.GameStatus.completed);
//        gameInstanceRepository.save(gameInstance);
//    }
//
//    private void handleSideShow(List<GamePlayer> activePlayers, Long gameId) {
//        // Logic to handle side show
//        // This could involve additional rounds of evaluation or player actions
//        // For now, let's broadcast a message indicating a side show has occurred
//        broadcastEvent("SIDE_SHOW", gameId);
//    }
//
//    protected void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}




//    private GamePlayer determineWinner(List<GamePlayer> activePlayers) {
//        // Assuming we have a method to evaluate hand strengths
//        GamePlayer player1 = activePlayers.get(0);
//        GamePlayer player2 = activePlayers.get(1);
//
//        if (evaluateHandStrength(player1.getCards()) > evaluateHandStrength(player2.getCards())) {
//            return player1;
//        } else {
//            return player2;
//        }
//    }

//    private int evaluateHandStrength(String cards) {
//        try {
//            // Split the cards by comma and clean up any whitespace
//            List<Card> playerCards = convertStringToHand(Arrays.asList(cards.split(",")));
//
//            // Check if we got valid cards
//            if (playerCards.isEmpty()) {
//                throw new IllegalArgumentException("No valid cards found for evaluation.");
//            }
//
//            return CardUtils.calculateHandStrength(playerCards);
//        } catch (IllegalArgumentException e) {
//            System.err.println("Invalid card format: " + e.getMessage());
//            throw new IllegalStateException("Error evaluating hand strength: " + e.getMessage(), e);
//        } catch (Exception e) {
//            System.err.println("An unexpected error occurred while evaluating hand strength: " + e.getMessage());
//            throw new IllegalStateException("An error occurred while evaluating hand strength.", e);
//        }
//    }

//    public static List<Card> convertStringToHand(List<String> cardStrings) {
//        List<Card> hand = new ArrayList<>();
//
//        for (String cardString : cardStrings) {
//            // Clean up the card string: Remove brackets and quotes
//            cardString = cardString.replaceAll("[\\[\\]\"]", "").trim();
//
//            // Validate card string format: "rank of suit"
//            if (!cardString.matches("^(2|3|4|5|6|7|8|9|10|J|Q|K|A) of (Hearts|Diamonds|Clubs|Spades)$")) {
//                throw new IllegalArgumentException("Invalid card format: " + cardString);
//            }
//
//            // Split the card string into rank and suit
//            String[] parts = cardString.split(" of ");
//            if (parts.length == 2) {
//                String rank = parts[0].trim();
//                String suit = parts[1].trim();
//                hand.add(new Card(rank, suit)); // Assuming Card has a constructor accepting rank and suit
//            } else {
//                throw new IllegalArgumentException("Invalid card format: " + cardString);
//            }
//        }
//
//        return hand;
//    }

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
//        // Handle the case where there are exactly 2 active players
//        if (activePlayers.size() == 2) {
//            PlayerGame player1 = activePlayers.get(0);
//            PlayerGame player2 = activePlayers.get(1);
//
//            List<Card> hand1 = convertStringToHand(Arrays.asList(player1.getCards().split(",")));
//            List<Card> hand2 = convertStringToHand(Arrays.asList(player2.getCards().split(",")));
//
//            String handType1 = HandEvaluator.evaluateHand(hand1); // Assuming HandEvaluator exists
//            String handType2 = HandEvaluator.evaluateHand(hand2);
//
//            // Compare the hands
//            int comparisonResult = HandEvaluator.compareHands(handType1, handType2);
//
//            if (comparisonResult > 0) {
//                // Player 1 wins
//                player1.setResult(GameResult.WON);
//                player2.setResult(GameResult.LOST);
//                announceWinners(Collections.singletonList(player1));
//                return GameResult.WON; // Player 1 is the winner
//            } else if (comparisonResult < 0) {
//                // Player 2 wins
//                player1.setResult(GameResult.LOST);
//                player2.setResult(GameResult.WON);
//                announceWinners(Collections.singletonList(player2));
//                return GameResult.WON; // Player 2 is the winner
//            } else {
//                // It's a tie, handle accordingly
//                System.out.println("No clear winner due to a tie between Player "
//                        + player1.getUserId() + " and Player "
//                        + player2.getUserId());
//                return GameResult.PENDING; // No clear winner
//            }
//        }
//
//        // If there are more than two active players, handle as before
//        Map<String, String> playerResults = new HashMap<>();
//        String bestHand = null;
//        PlayerGame singleWinner = null;
//
//        // Evaluate hands for active players
//        for (PlayerGame playerGame : activePlayers) {
//            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
//            String handType = HandEvaluator.evaluateHand(hand);
//            playerResults.put(playerGame.getUserId().toString(), handType);
//        }
//
//        // Find the best hand
//        for (PlayerGame playerGame : activePlayers) {
//            String currentHandType = playerResults.get(playerGame.getUserId().toString());
//
//            if (bestHand == null || HandEvaluator.compareHands(currentHandType, bestHand) > 0) {
//                bestHand = currentHandType;
//                singleWinner = playerGame; // Set this player as the current best player
//            } else if (HandEvaluator.compareHands(currentHandType, bestHand) == 0) {
//                // If there's a tie, set singleWinner to null to indicate no clear winner
//                singleWinner = null;
//            }
//        }
//
//        // Announce the winner
//        if (singleWinner != null) {
//            announceWinners(Collections.singletonList(singleWinner)); // Announce the single winner
//            return GameResult.WON; // There is a clear winner
//        } else {
//            System.out.println("No clear winner due to a tie.");
//            return GameResult.PENDING; // No clear winner
//        }
//    }

//    private void updatePlayerGameResults(List<PlayerGame> playersInGame, List<PlayerGame> activePlayers) {
//        // Determine winners among active players
//        List<PlayerGame> winners = determineWinners(activePlayers);
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (playerGame.getHasFolded()) {
//                playerGame.setResult(GameResult.LOST); // If player has folded, they lost
//            } else if (winners.contains(playerGame)) {
//                playerGame.setResult(GameResult.WON); // If player is a winner
//            } else {
//                playerGame.setResult(GameResult.LOST); // If player is a loser
//            }
//            playerGameRepository.save(playerGame); // Save the updated player game state
//        }
//    }
//
//    private List<PlayerGame> determineWinners(List<PlayerGame> activePlayers) {
//        if (activePlayers.isEmpty()) {
//            return Collections.emptyList(); // No winners if there are no active players
//        }
//
//        Map<String, String> playerResults = new HashMap<>();
//        PlayerGame bestPlayer = null;
//        String bestHand = null;
//
//        // Evaluate hands for active players and determine the best hand
//        for (PlayerGame playerGame : activePlayers) {
//            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
//            String handType = HandEvaluator.evaluateHand(hand); // Assuming HandEvaluator exists
//
//            // Determine if this hand is the best
//            if (bestHand == null || HandEvaluator.compareHands(handType, bestHand) > 0) {
//                bestHand = handType;
//                bestPlayer = playerGame;
//            }
//        }
//
//        // Return the best player as the winner
//        return bestPlayer != null ? Collections.singletonList(bestPlayer) : Collections.emptyList();
//    }
//
//    private void announceWinners(List<PlayerGame> winners) {
//        if (winners.isEmpty()) {
//            System.out.println("No winners this round.");
//        } else {
//            StringBuilder winnerMessage = new StringBuilder("Winner: ");
//            for (PlayerGame winner : winners) {
//                winnerMessage.append("Player ").append(winner.getUserId()).append(", ");
//            }
//            // Remove last comma and space
//            winnerMessage.setLength(winnerMessage.length() - 2);
//            System.out.println(winnerMessage.toString());
//        }
//    }
//
//
//    private List<Card> convertStringToHand(List<String> handString) {
//        List<Card> hand = new ArrayList<>();
//        for (String cardString : handString) {
//            String[] parts = cardString.split(" of ");
//            String rank = parts[0].trim();
//            String suit = parts[1].trim();
//            hand.add(new Card(suit, rank)); // Card object takes suit and rank
//        }
//        return hand;
//    }
//}
//
////
////import com.auth.AuthImpl.ctp.entity.Game;
////import com.auth.AuthImpl.ctp.entity.PlayerGame;
////import com.auth.AuthImpl.ctp.enums.GameResult;
////import com.auth.AuthImpl.ctp.enums.GameStatus;
////import com.auth.AuthImpl.ctp.model.Card;
////import com.auth.AuthImpl.ctp.repository.GameRepository;
////import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
////import com.auth.AuthImpl.ctp.service.HandEvaluator;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.messaging.simp.SimpMessagingTemplate;
////import org.springframework.stereotype.Service;
////
////import java.util.*;
////
////@Service
////public class ShowCardsAction {
////
////    @Autowired
////    private PlayerGameRepository playerGameRepository;
////
////    @Autowired
////    private GameRepository gameRepository;
////
////    @Autowired
////    private SimpMessagingTemplate messagingTemplate;
////
////    // Map to track side show requests
////    private Map<Long, List<PlayerGame>> sideShowRequests = new HashMap<>();
////
////    public List<String> showCards(Long gameId) {
////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
////        List<String> visibleCards = new ArrayList<>();
////        List<PlayerGame> activePlayers = filterActivePlayers(playersInGame, visibleCards);
////
////        if (activePlayers.isEmpty()) {
////            updatePlayerGameResults(playersInGame, Collections.emptyList());
////            markGameAsCompleted(gameId);
////            return visibleCards;
////        }
////
////        // Determine winner if only two active players
////        if (activePlayers.size() == 2) {
////            GameResult gameResult = determineWinner(activePlayers);
////            updatePlayerGameResults(playersInGame, activePlayers);
////            markGameAsCompleted(gameId);
////            broadcastEvent("NATURAL_SHOW", gameId);
////            return visibleCards;
////        }
////
////        // Handle side show for more than two active players
////        handleSideShow(activePlayers, gameId);
////        return visibleCards;
////    }
////
////    private void handleSideShow(List<PlayerGame> activePlayers, Long gameId) {
////        gatherSideShowRequests(activePlayers);
////
////        if (sideShowRequests.isEmpty()) {
////            broadcastEvent("NO_SIDE_SHOW", gameId);
////            return;
////        }
////
////        for (Map.Entry<Long, List<PlayerGame>> entry : sideShowRequests.entrySet()) {
////            List<PlayerGame> participants = entry.getValue();
////            if (participants.size() < 2) {
////                broadcastEvent("INSUFFICIENT_PLAYERS_FOR_SIDE_SHOW", gameId);
////                continue;
////            }
////
////            broadcastSideShowParticipants(participants);
////            GameResult sideShowResult = determineWinner(participants);
////            updatePlayerGameResults(activePlayers, participants);
////            broadcastEvent("SIDE_SHOW_RESULT", sideShowResult);
////        }
////
////        sideShowRequests.clear(); // Clear requests after processing
////    }
////
////    private void gatherSideShowRequests(List<PlayerGame> activePlayers) {
////        for (int i = 1; i < activePlayers.size(); i++) {
////            PlayerGame requestingPlayer = activePlayers.get(i);
////            PlayerGame previousPlayer = activePlayers.get(i - 1);
////
////            if (!requestingPlayer.getHasFolded() && new Random().nextBoolean()) {
////                System.out.println("Player " + requestingPlayer.getUserId() + " has requested a side show with Player " + previousPlayer.getUserId());
////
////                // Ask the previous player if they accept the side show request
////                if (askForSideShowAcceptance(previousPlayer)) {
////                    System.out.println("Player " + previousPlayer.getUserId() + " accepted the side show request.");
////                    handleSideShowResult(requestingPlayer, previousPlayer);
////                } else {
////                    System.out.println("Player " + previousPlayer.getUserId() + " denied the side show request.");
////                }
////            }
////        }
////    }
////
////    private boolean askForSideShowAcceptance(PlayerGame previousPlayer) {
////        return new Random().nextBoolean(); // Simulate acceptance/denial
////    }
////
////    private void handleSideShowResult(PlayerGame requestingPlayer, PlayerGame previousPlayer) {
////        List<Card> hand1 = convertStringToHand(Arrays.asList(requestingPlayer.getCards().split(",")));
////        List<Card> hand2 = convertStringToHand(Arrays.asList(previousPlayer.getCards().split(",")));
////
////        int comparisonResult = HandEvaluator.compareHands(HandEvaluator.evaluateHand(hand1), HandEvaluator.evaluateHand(hand2));
////
////        if (comparisonResult > 0) {
////            System.out.println("Player " + previousPlayer.getUserId() + " loses side show and folds.");
////            previousPlayer.setHasFolded(true);
////        } else {
////            System.out.println("Player " + requestingPlayer.getUserId() + " loses side show and folds.");
////            requestingPlayer.setHasFolded(true);
////        }
////
////        playerGameRepository.save(requestingPlayer);
////        playerGameRepository.save(previousPlayer);
////    }
////
////    private GameResult determineWinner(List<PlayerGame> activePlayers) {
////        if (activePlayers.size() == 1) {
////            announceWinners(Collections.singletonList(activePlayers.get(0)));
////            return GameResult.WON;
////        }
////
////        PlayerGame bestPlayer = null;
////        String bestHand = null;
////
////        for (PlayerGame playerGame : activePlayers) {
////            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
////            String handType = HandEvaluator.evaluateHand(hand);
////
////            if (bestHand == null || HandEvaluator.compareHands(handType, bestHand) > 0) {
////                bestHand = handType;
////                bestPlayer = playerGame;
////            } else if (HandEvaluator.compareHands(handType, bestHand) == 0) {
////                bestPlayer = null; // Tie
////            }
////        }
////
////        if (bestPlayer != null) {
////            announceWinners(Collections.singletonList(bestPlayer));
////            return GameResult.WON;
////        } else {
////            broadcastEvent("TIE", activePlayers);
////            return GameResult.PENDING;
////        }
////    }
////
////    private List<PlayerGame> filterActivePlayers(List<PlayerGame> playersInGame, List<String> visibleCards) {
////        List<PlayerGame> activePlayers = new ArrayList<>();
////        for (PlayerGame playerGame : playersInGame) {
////            if (!playerGame.getHasFolded()) {
////                visibleCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
////                activePlayers.add(playerGame);
////            }
////        }
////        return activePlayers;
////    }
////
////    private void updatePlayerGameResults(List<PlayerGame> playersInGame, List<PlayerGame> activePlayers) {
////        List<PlayerGame> winners = determineWinners(activePlayers);
////
////        for (PlayerGame playerGame : playersInGame) {
////            if (playerGame.getHasFolded()) {
////                playerGame.setResult(GameResult.LOST);
////            } else if (winners.contains(playerGame)) {
////                playerGame.setResult(GameResult.WON);
////            } else {
////                playerGame.setResult(GameResult.LOST);
////            }
////            playerGameRepository.save(playerGame);
////        }
////    }
////
////    private List<PlayerGame> determineWinners(List<PlayerGame> activePlayers) {
////        if (activePlayers.isEmpty()) {
////            return Collections.emptyList();
////        }
////
////        PlayerGame bestPlayer = null;
////        String bestHand = null;
////
////        for (PlayerGame playerGame : activePlayers) {
////            List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
////            String handType = HandEvaluator.evaluateHand(hand);
////
////            if (bestHand == null || HandEvaluator.compareHands(handType, bestHand) > 0) {
////                bestHand = handType;
////                bestPlayer = playerGame;
////            }
////        }
////
////        return bestPlayer != null ? Collections.singletonList(bestPlayer) : Collections.emptyList();
////    }
////
////    // Mark game as "COMPLETED" and broadcast the event
////    private void markGameAsCompleted(Long gameId) {
////        Optional<Game> game = gameRepository.findById(gameId);
////        if (game.isPresent()) {
////            Game currentGame = game.get();
////            currentGame.setStatus(GameStatus.COMPLETED);
////            gameRepository.save(currentGame);
////            broadcastEvent("GAME_COMPLETED", gameId);
////        }
////    }
////
////    private void announceWinners(List<PlayerGame> winners) {
////        if (winners.isEmpty()) {
////            System.out.println("No winners this round.");
////        } else {
////            StringBuilder winnerMessage = new StringBuilder("Winner: ");
////            for (PlayerGame winner : winners) {
////                winnerMessage.append("Player ").append(winner.getUserId()).append(", ");
////            }
////            winnerMessage.setLength(winnerMessage.length() - 2); // Remove last comma and space
////            System.out.println(winnerMessage.toString());
////        }
////    }
////
////    private void broadcastSideShowParticipants(List<PlayerGame> participants) {
////        List<String> sideShowCards = new ArrayList<>();
////        for (PlayerGame playerGame : participants) {
////            sideShowCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
////        }
////
////        broadcastEvent("SIDE_SHOW_PARTICIPANTS", sideShowCards);
////    }
////
////    private void broadcastEvent(String eventType, Object data) {
////        GameEvent event = new GameEvent();
////        event.setEventType(eventType);
////        event.setEventData(data);
////        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
////    }
////
////    private List<Card> convertStringToHand(List<String> handString) {
////        List<Card> hand = new ArrayList<>();
////        for (String cardString : handString) {
////            String[] parts = cardString.split(" of ");
////            String rank = parts[0].trim();
////            String suit = parts[1].trim();
////            hand.add(new Card(suit, rank)); // Card object takes suit and rank
////        }
////        return hand;
////    }
////}
