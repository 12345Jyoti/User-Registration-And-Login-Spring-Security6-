package com.auth.AuthImpl.ctp.actionImpl;
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
    private void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }
}