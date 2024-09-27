package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.entity.Player;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameResult;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.model.Card;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.ctp.service.HandEvaluator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class PlaceBetAction {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    private int currentPot = 0;
//    private int currentBet = 0;
//
//    public void placeBet(Long playerId, int amount, Long gameId) {
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//        Player player = playerRepository.findById(playerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//        // Ensure the player has enough chips to place the bet
//        if (player.getChips() < amount) {
//            throw new IllegalArgumentException("Insufficient chips to place the bet");
//        }
//
//        // Subtract the bet amount from the player's available chips
//        player.setChips(player.getChips() - amount);
//        playerRepository.save(player);
//
//        // Set the current bet to the max bet placed and update the pot
//        currentBet = Math.max(currentBet, amount);
//        currentPot += amount;
//
//        // Update the player's bet amount
//        playerGame.setBetAmount(playerGame.getBetAmount() + amount);
//        playerGameRepository.save(playerGame);
//
//        // Log the bet placed
//        System.out.println("Player " + playerId + " placed a bet of " + amount + " in game " + gameId);
//
//        // Broadcast the betting event
//        broadcastEvent("BET_PLACED", Map.of("playerId", playerId, "amount", amount));
//
//        // Check if the betting round is over
//        if (isBettingRoundOver(gameId)) {
//            finishBettingRound(gameId);
//        }
//    }
//
//    private boolean isBettingRoundOver(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        // Check if all players either folded or matched the current bet
//        return playersInGame.stream().allMatch(playerGame ->
//                playerGame.getHasFolded() || playerGame.getBetAmount() >= currentBet
//        );
//    }
//
//    public void finishBettingRound(Long gameId) {
//        String winnerId = evaluateHands(gameId);
//        distributePot(Long.parseLong(winnerId), gameId);
//        setPlayerResults(gameId, winnerId);
//        endGame(gameId);
//
//        // Broadcast round finished event
//        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
//
//        // Reset current bet and pot after the round finishes
//        resetRoundState();
//    }
//
//    private void resetRoundState() {
//        currentBet = 0;
//        currentPot = 0;
//    }
//
//    private void setPlayerResults(Long gameId, String winnerId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (playerGame.getUserId().toString().equals(winnerId)) {
//                playerGame.setResult(GameResult.WON);  // Mark the winner
//            } else if (!playerGame.getHasFolded()) {
//                playerGame.setResult(GameResult.LOST); // Mark active (non-folded) players as lost
//            }
//            playerGameRepository.save(playerGame);
//        }
//    }
//
//    public void distributePot(Long winnerId, Long gameId) {
//        PlayerGame winnerGame = playerGameRepository.findByUserIdAndGameId(winnerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Winner not found"));
//        Player winner = playerRepository.findById(winnerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//        // Add the pot to the winner's chips and save
//        winner.setChips(winner.getChips() + currentPot);
//        playerRepository.save(winner);
//    }
//
//    public void endGame(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        game.setStatus(GameStatus.COMPLETED);
//        gameRepository.save(game);
//    }
//
//    private String evaluateHands(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        Map<String, String> playerResults = new HashMap<>();
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (!playerGame.getHasFolded()) {
//                // Convert cards from string format and evaluate hands
//                List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
//                String handType = HandEvaluator.evaluateHand(hand);  // Assuming HandEvaluator is implemented
//                playerResults.put(playerGame.getUserId().toString(), handType);
//            }
//        }
//
//        return determineBestHand(playerResults);
//    }
//
//    private String determineBestHand(Map<String, String> playerResults) {
//        // Define hand hierarchy (e.g., "TRAIL" > "PURE SEQUENCE" > "SEQUENCE" > ...)
//        List<String> handHierarchy = Arrays.asList("HIGH CARD", "PAIR", "COLOR", "SEQUENCE", "PURE SEQUENCE", "TRAIL");
//        String bestPlayer = null;
//        String bestHandType = null;
//
//        // Determine the best hand based on the hierarchy
//        for (Map.Entry<String, String> entry : playerResults.entrySet()) {
//            String playerId = entry.getKey();
//            String handType = entry.getValue();
//
//            if (bestHandType == null || handHierarchy.indexOf(handType) > handHierarchy.indexOf(bestHandType)) {
//                bestHandType = handType;
//                bestPlayer = playerId;
//            }
//        }
//        return bestPlayer;
//    }
//
//    private List<Card> convertStringToHand(List<String> handString) {
//        List<Card> hand = new ArrayList<>();
//        for (String cardString : handString) {
//            String[] parts = cardString.split(" of ");
//            String rank = parts[0].trim();
//            String suit = parts[1].trim();
//            hand.add(new Card(suit, rank));  // Card object takes suit and rank
//        }
//        return hand;
//    }
//
//    private void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}

//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.entity.Player;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameResult;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.model.Card;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.ctp.service.HandEvaluator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class PlaceBetAction {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    @Autowired
//    private TurnManager turnManager; // Injecting TurnManager
//
//    private static final int ANTE_AMOUNT = 100;  // Minimum ante amount required for the first bet
//
//    private int currentPot = 0;
//    private int currentBet = 0;  // Tracks the highest bet placed in the current round
//
//    public void placeBet(Long playerId, int amount, Long gameId, boolean isBlind) {
//        System.out.println("Attempting to place a bet...");
//        System.out.println("Player ID: " + playerId + " | Game ID: " + gameId + " | Bet Amount: " + amount + " | Blind: " + isBlind);
//
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//        Player player = playerRepository.findById(playerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//        // Check current player
//        Long currentPlayerId = turnManager.getCurrentPlayerId(gameId);
//        if (currentPlayerId == null) {
//            throw new IllegalStateException("Current player is not set. Please check game initialization.");
//        }
//
//        if (!currentPlayerId.equals(playerId)) {
//            System.out.println("It's not the player's turn. Current player ID: " + currentPlayerId);
//            throw new IllegalStateException("It's not your turn!");
//        }
//
//        // Validation: Ensure the game is in the betting phase
//        if (!game.getStatus().equals(GameStatus.ACTIVE)) {
//            System.out.println("Game is not in progress. Current game status: " + game.getStatus());
//            throw new IllegalStateException("Game is not in the betting phase.");
//        }
//
//        // Validation: Ensure the player has enough chips to place the bet
//        if (player.getChips() < amount) {
//            System.out.println("Player " + playerId + " does not have enough chips to place the bet.");
//            throw new IllegalArgumentException("Insufficient chips to place the bet");
//        }
//
//        // Additional Validation: Ensure the first bet is at least the ANTE_AMOUNT
//        if (currentBet == 0 && amount < ANTE_AMOUNT) {
//            System.out.println("First bet is below the ANTE_AMOUNT. Current bet: " + amount + " | ANTE_AMOUNT: " + ANTE_AMOUNT);
//            throw new IllegalArgumentException("The first bet must be at least " + ANTE_AMOUNT);
//        }
//
//        // Validation: Ensure the bet is valid (blind and seen players have different rules)
//        validateBet(playerGame, amount, isBlind);
//
//        // Process the bet
//        processBet(playerGame, player, amount);
//
//        // Broadcast the betting event
//        broadcastEvent("BET_PLACED", Map.of("playerId", playerId, "amount", amount));
//
//        // Move to the next player
//        moveToNextPlayer(gameId);
//
//        // After moving to the next player, check if the round is over
//        if (isBettingRoundOver(gameId)) {
//            System.out.println("Betting round is over for game ID: " + gameId);
//            finishBettingRound(gameId);
//        } else {
//            System.out.println("Betting round continues for game ID: " + gameId);
//        }
//    }
//
//
//    private void validateBet(PlayerGame playerGame, int amount, boolean isBlind) {
//        int minBet = currentBet == 0 ? ANTE_AMOUNT : currentBet;
//        int maxBet = minBet * 2;  // Usually, bets can be double the previous bet
//
//        if (isBlind) {
//            // Blind players can bet the minimum or double the minimum
//            if (amount < minBet || amount > maxBet) {
//                System.out.println("Invalid blind bet. Amount: " + amount + " | MinBet: " + minBet + " | MaxBet: " + maxBet);
//                throw new IllegalArgumentException("Blind bet must be between " + minBet + " and " + maxBet);
//            }
//        } else {
//            // Seen players must bet at least twice the current bet
//            if (amount < minBet * 2) {
//                System.out.println("Invalid seen player bet. Amount: " + amount + " | MinBet for seen players: " + (minBet * 2));
//                throw new IllegalArgumentException("Seen players must bet at least " + (minBet * 2));
//            }
//        }
//
//        System.out.println("Bet validated. Amount: " + amount + " | Blind: " + isBlind);
//    }
//
//    private void processBet(PlayerGame playerGame, Player player, int amount) {
//        // Subtract the bet amount from the player's available chips
//        player.setChips(player.getChips() - amount);
//        playerRepository.save(player);
//
//        // Update the current bet and pot
//        currentBet = Math.max(currentBet, amount);
//        currentPot += amount;
//
//        System.out.println("Processed bet for player " + player.getPlayerId() + ". Bet Amount: " + amount + " | Current Pot: " + currentPot + " | Current Bet: " + currentBet);
//
//        // Update the player's bet amount
//        playerGame.setBetAmount(playerGame.getBetAmount() + amount);
//        playerGameRepository.save(playerGame);
//    }
//
//    private boolean isBettingRoundOver(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        // Check if all players either folded or matched the current bet
//        boolean allPlayersMatched = playersInGame.stream().allMatch(playerGame ->
//                playerGame.getHasFolded() || playerGame.getBetAmount() >= currentBet
//        );
//        System.out.println("Is betting round over? " + allPlayersMatched);
//        return allPlayersMatched;
//    }
//
//    public void finishBettingRound(Long gameId) {
//        System.out.println("Finishing betting round for game ID: " + gameId);
//        String winnerId = evaluateHands(gameId);
//        System.out.println("Winner for the round: Player " + winnerId);
//
//        distributePot(Long.parseLong(winnerId), gameId);
//        setPlayerResults(gameId, winnerId);
//        endGame(gameId);
//
//        // Broadcast round finished event
//        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot, "gameId", gameId));
//
//        // Reset current bet and pot after the round finishes
//        resetRoundState();
//    }
//
//    private void resetRoundState() {
//        System.out.println("Resetting round state. Current pot: " + currentPot + " | Current bet: " + currentBet);
//        currentBet = 0;
//        currentPot = 0;
//    }
//
//    public void distributePot(Long winnerId, Long gameId) {
//        PlayerGame winnerGame = playerGameRepository.findByUserIdAndGameId(winnerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Winner not found"));
//        Player winner = playerRepository.findById(winnerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//        winner.setChips(winner.getChips() + currentPot);
//        playerRepository.save(winner);
//
//        System.out.println("Distributing pot of " + currentPot + " to player " + winnerId);
//    }
//
//    private void setPlayerResults(Long gameId, String winnerId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (playerGame.getUserId().toString().equals(winnerId)) {
//                playerGame.setResult(GameResult.WON);
//            } else {
//                playerGame.setResult(GameResult.LOST);
//            }
//            playerGameRepository.save(playerGame);
//        }
//    }
//
//    private void endGame(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//        game.setStatus(GameStatus.COMPLETED);
//        gameRepository.save(game);
//    }
//
//    private void broadcastEvent(String eventType, Map<String, Object> eventData) {
//        GameEvent event = new GameEvent();
//        // Setting relevant data to the event object if needed
//        messagingTemplate.convertAndSend("/topic/game/" + eventData.get("gameId"), event);
//        System.out.println("Broadcasting event: " + eventType + " with data: " + eventData);
//    }
//
//    private void moveToNextPlayer(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        // Get the current player's ID from the TurnManager
//        Long currentPlayerId = turnManager.getCurrentPlayerId(gameId);
//
//        // Find the current player in the list
//        PlayerGame currentPlayer = playersInGame.stream()
//                .filter(playerGame -> playerGame.getUserId().equals(currentPlayerId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("No current player found"));
//
//        int currentIndex = playersInGame.indexOf(currentPlayer);
//        int nextIndex = (currentIndex + 1) % playersInGame.size();
//
//        // Find the next player who hasn't folded
//        while (playersInGame.get(nextIndex).getHasFolded()) {
//            nextIndex = (nextIndex + 1) % playersInGame.size();
//        }
//
//        // Update the TurnManager with the next player's ID
//        Long nextPlayerId = playersInGame.get(nextIndex).getUserId();
//        turnManager.setCurrentPlayerId(gameId, nextPlayerId);
//        System.out.println("Moved to next player. Next player ID: " + nextPlayerId);
//    }
//
//    private String evaluateHands(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        Map<String, String> playerResults = new HashMap<>();
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (!playerGame.getHasFolded()) {
//                // Convert cards from string format and evaluate hands
//                List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
//                String handType = HandEvaluator.evaluateHand(hand);  // Assuming HandEvaluator is implemented
//                playerResults.put(playerGame.getUserId().toString(), handType);
//            }
//        }
//
//        return determineBestHand(playerResults);
//    }
//
//    private String determineBestHand(Map<String, String> playerResults) {
//        // Define hand hierarchy (e.g., "TRAIL" > "PURE SEQUENCE" > "SEQUENCE" > ...)
//        List<String> handHierarchy = Arrays.asList("HIGH CARD", "PAIR", "COLOR", "SEQUENCE", "PURE SEQUENCE", "TRAIL");
//        String bestPlayer = null;
//        String bestHandType = null;
//
//        // Determine the best hand based on the hierarchy
//        for (Map.Entry<String, String> entry : playerResults.entrySet()) {
//            String playerId = entry.getKey();
//            String handType = entry.getValue();
//
//            if (bestHandType == null || handHierarchy.indexOf(handType) > handHierarchy.indexOf(bestHandType)) {
//                bestHandType = handType;
//                bestPlayer = playerId;
//            }
//        }
//        return bestPlayer;
//    }

//    private List<Card> convertStringToHand(List<String> handString) {
//        List<Card> hand = new ArrayList<>();
//        for (String cardString : handString) {
//            String[] parts = cardString.split(" of ");
//            String rank = parts[0].trim();
//            String suit = parts[1].trim();
//            hand.add(new Card(suit, rank));  // Card object takes suit and rank
//        }
//        return hand;
//    }
//}
import com.auth.AuthImpl.ctp.actionImpl.TurnManager;
import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.entity.Game;
import com.auth.AuthImpl.ctp.entity.Player;
import com.auth.AuthImpl.ctp.entity.PlayerGame;
import com.auth.AuthImpl.ctp.enums.GameResult;
import com.auth.AuthImpl.ctp.enums.GameStatus;
import com.auth.AuthImpl.ctp.model.Card;
import com.auth.AuthImpl.ctp.repository.GameRepository;
import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import com.auth.AuthImpl.ctp.service.HandEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlaceBetAction {

    @Autowired
    private PlayerGameRepository playerGameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TurnManager turnManager;

    private static final int ANTE_AMOUNT = 100;

    private int currentPot = 0;
    private int currentBet = 0;

    public void placeBet(Long playerId, int amount, Long gameId, boolean isBlind) {
        System.out.println("Attempting to place a bet...");
        System.out.println("Player ID: " + playerId + " | Game ID: " + gameId + " | Bet Amount: " + amount + " | Blind: " + isBlind);

        // Fetch game and validate
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (game.getStatus() != GameStatus.ACTIVE) {
            throw new IllegalStateException("Game is not active.");
        }

        // Fetch player in game
        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));

        // Validate current player
        validateCurrentPlayer(playerId, gameId);

        // Ensure the player has enough chips
        validatePlayerChips(playerId, amount);

        // Ensure the first bet is at least the ANTE_AMOUNT
        validateFirstBet(amount);

        // Validate and process the bet
        validateBet(playerGame, amount, isBlind);
        processBet(playerGame, amount);

        // Broadcast the betting event and manage turn
        broadcastEvent("BET_PLACED", Map.of("playerId", playerId, "amount", amount));
        moveToNextPlayer(gameId);

        // Check if the round is over
        if (isBettingRoundOver(gameId)) {
            finishBettingRound(gameId);
        } else {
            System.out.println("Betting round continues for game ID: " + gameId);
        }
    }

    private void validateCurrentPlayer(Long playerId, Long gameId) {
        Long currentPlayerId = turnManager.getCurrentPlayerId(gameId);
        if (currentPlayerId == null || !currentPlayerId.equals(playerId)) {
            System.out.println("It's not the player's turn. Current player ID: " + currentPlayerId);
            throw new IllegalStateException("It's not your turn!");
        }
    }

    private void validatePlayerChips(Long playerId, int amount) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        if (player.getChips() < amount) {
            System.out.println("Player " + playerId + " does not have enough chips to place the bet.");
            throw new IllegalArgumentException("Insufficient chips to place the bet");
        }
    }

    private void validateFirstBet(int amount) {
        if (currentBet == 0 && amount < ANTE_AMOUNT) {
            System.out.println("First bet is below the ANTE_AMOUNT. Current bet: " + amount + " | ANTE_AMOUNT: " + ANTE_AMOUNT);
            throw new IllegalArgumentException("The first bet must be at least " + ANTE_AMOUNT);
        }
    }

    private void validateBet(PlayerGame playerGame, int amount, boolean isBlind) {
        int minBet = currentBet == 0 ? ANTE_AMOUNT : currentBet;
        int maxBet = minBet * 2;

        if (isBlind) {
            if (amount < minBet || amount > maxBet) {
                System.out.println("Invalid blind bet. Amount: " + amount + " | MinBet: " + minBet + " | MaxBet: " + maxBet);
                throw new IllegalArgumentException("Blind bet must be between " + minBet + " and " + maxBet);
            }
        } else {
            if (amount < minBet * 2) {
                System.out.println("Invalid seen player bet. Amount: " + amount + " | MinBet for seen players: " + (minBet * 2));
                throw new IllegalArgumentException("Seen players must bet at least " + (minBet * 2));
            }
        }

        System.out.println("Bet validated. Amount: " + amount + " | Blind: " + isBlind);
    }

    private void processBet(PlayerGame playerGame, int amount) {
        // Subtract the bet amount from the player's available chips
        Player player = playerRepository.findById(playerGame.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        player.setChips(player.getChips() - amount);
        playerRepository.save(player);

        // Update the current bet and pot
        currentBet = Math.max(currentBet, amount);
        currentPot += amount;

        System.out.println("Processed bet for player " + player.getPlayerId() + ". Bet Amount: " + amount + " | Current Pot: " + currentPot + " | Current Bet: " + currentBet);

        // Update the player's bet amount
        playerGame.setBetAmount(playerGame.getBetAmount() + amount);
        playerGameRepository.save(playerGame);
    }

    private boolean isBettingRoundOver(Long gameId) {
        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
        boolean allPlayersMatched = playersInGame.stream().allMatch(playerGame ->
                playerGame.getHasFolded() || playerGame.getBetAmount() >= currentBet
        );
        System.out.println("Is betting round over? " + allPlayersMatched);
        return allPlayersMatched;
    }

    public void finishBettingRound(Long gameId) {
        System.out.println("Finishing betting round for game ID: " + gameId);
        String winnerId = evaluateHands(gameId);
        System.out.println("Winner for the round: Player " + winnerId);

        distributePot(Long.parseLong(winnerId), gameId);
        setPlayerResults(gameId, winnerId);
        endGame(gameId);

        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
        resetRoundState();
    }

    public void distributePot(Long winnerId, Long gameId) {
        PlayerGame winnerGame = playerGameRepository.findByUserIdAndGameId(winnerId, gameId)
                .orElseThrow(() -> new IllegalArgumentException("Winner not found"));
        Player winner = playerRepository.findById(winnerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        winner.setChips(winner.getChips() + currentPot);
        playerRepository.save(winner);

        System.out.println("Distributing pot of " + currentPot + " to player " + winnerId);
    }

    private void setPlayerResults(Long gameId, String winnerId) {
        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
        for (PlayerGame playerGame : playersInGame) {
            playerGame.setResult(playerGame.getUserId().toString().equals(winnerId) ? GameResult.WON : GameResult.LOST);
            playerGameRepository.save(playerGame);
        }
    }

    private void resetRoundState() {
        System.out.println("Resetting round state. Current pot: " + currentPot + " | Current bet: " + currentBet);
        currentBet = 0;
        currentPot = 0;
    }

    private void broadcastEvent(String eventType, Map<String, Object> payload) {
        messagingTemplate.convertAndSend("/topic/game-events", new GameEvent());
        System.out.println("Broadcasted event: " + eventType);
    }

    private void moveToNextPlayer(Long gameId) {
        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
        turnManager.moveToNextPlayer(gameId);
    }

    private void endGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        game.setStatus(GameStatus.COMPLETED);
        gameRepository.save(game);
        System.out.println("Game ID " + gameId + " ended.");
    }

    private String evaluateHands(Long gameId) {
        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
        Map<String, String> playerResults = new HashMap<>();

        for (PlayerGame playerGame : playersInGame) {
            if (!playerGame.getHasFolded()) {
                // Convert cards from string format and evaluate hands
                List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
                String handType = HandEvaluator.evaluateHand(hand);  // Assuming HandEvaluator is implemented
                playerResults.put(playerGame.getUserId().toString(), handType);
            }
        }

        return determineBestHand(playerResults);
    }

    private String determineBestHand(Map<String, String> playerResults) {
        // Define hand hierarchy (e.g., "TRAIL" > "PURE SEQUENCE" > "SEQUENCE" > ...)
        List<String> handHierarchy = Arrays.asList("HIGH CARD", "PAIR", "COLOR", "SEQUENCE", "PURE SEQUENCE", "TRAIL");
        String bestPlayer = null;
        String bestHandType = null;

        // Determine the best hand based on the hierarchy
        for (Map.Entry<String, String> entry : playerResults.entrySet()) {
            String playerId = entry.getKey();
            String handType = entry.getValue();

            if (bestHandType == null || handHierarchy.indexOf(handType) > handHierarchy.indexOf(bestHandType)) {
                bestHandType = handType;
                bestPlayer = playerId;
            }
        }
        return bestPlayer;
    }

    private List<Card> convertStringToHand(List<String> handString) {
        List<Card> hand = new ArrayList<>();
        for (String cardString : handString) {
            String[] parts = cardString.split(" of ");
            String rank = parts[0].trim();
            String suit = parts[1].trim();
            hand.add(new Card(suit, rank));  // Card object takes suit and rank
        }
        return hand;
    }
}