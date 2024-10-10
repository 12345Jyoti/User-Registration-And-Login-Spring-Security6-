package com.auth.AuthImpl.ctp.actionImpl;
import com.auth.AuthImpl.ctp.dto.GameEvent;

import com.auth.AuthImpl.ctp.enums.GameCurrentStatus;
import com.auth.AuthImpl.ctp.enums.GameStatus;
import com.auth.AuthImpl.ctp.enums.PlayerGameResult;
import com.auth.AuthImpl.ctp.model.Card;
import com.auth.AuthImpl.ctp.nenity.GameInstance;
import com.auth.AuthImpl.ctp.nenity.GamePlayer;
import com.auth.AuthImpl.ctp.nenity.GameResult;
import com.auth.AuthImpl.ctp.nenity.Player;
import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
import com.auth.AuthImpl.ctp.repository.GamePlayerRepository;
import com.auth.AuthImpl.ctp.repository.GameResultRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import com.auth.AuthImpl.ctp.service.HandEvaluator;
import com.auth.AuthImpl.registraion.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlaceBetAction {

//public class GameService {


    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private GameInstanceRepository gameInstanceRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameResultRepository gameResultRepository;
    @Autowired
    private TurnManager turnManager;

    private BigDecimal currentPot = BigDecimal.ZERO;
    private BigDecimal currentBet = BigDecimal.ZERO;
    private static final BigDecimal ANTE_AMOUNT = BigDecimal.valueOf(100); // Example ante amount
    private int currentPlayerIndex = 0;

    public void placeBet(Long gameId, Long playerId, BigDecimal betAmount) {
        System.out.println("Attempting to place a bet for Player ID: " + playerId + " | Bet Amount: " + betAmount);

        GamePlayer gamePlayer = getGamePlayer(gameId, playerId);
        GameInstance game = getGameInstance(gamePlayer.getGameId());

        validateGameState(game);
//        validateCurrentPlayer(playerId, game.getId());
        validatePlayerChips(playerId, betAmount);
        validateFirstBet(betAmount);
        validateBet(gamePlayer, betAmount);

        processBet(gamePlayer, betAmount);
        broadcastEvent("BET_PLACED", "Player " + playerId + " has placed a bet of " + betAmount);

        if (isBettingRoundOver(game.getId())) {
            finishBettingRound(game.getId());
        } else {
            System.out.println("Betting round continues for game ID: " + game.getId());
        }
    }

    private GamePlayer getGamePlayer(Long gameId,Long playerId) {
        return gamePlayerRepository.findByGameIdAndPlayerId(gameId,playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    private GameInstance getGameInstance(Long gameId) {
        return gameInstanceRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
    }

    private void validateGameState(GameInstance game) {
        if (game.getStatus() != Status.LIVE) {
            throw new IllegalStateException("Game is not active.");
        }
    }

//    private void finishBettingRound(Long gameId) {
//        System.out.println("Finishing betting round for game ID: " + gameId);
//
//        // Check if the betting round is over before proceeding
//        if (!isBettingRoundOver(gameId)) {
//            System.out.println("Betting round is not over yet.");
//            return; // Exit if betting is not complete
//        }
//
//        // Retrieve remaining players who have not folded
//        List<GamePlayer> remainingPlayers = gamePlayerRepository.findByGameIdAndGameCurrentStatusNot(gameId, GameCurrentStatus.fold);
//
//        // If only one player remains, they are the winner
//        if (remainingPlayers.size() == 1) {
//            String winnerId = remainingPlayers.get(0).getPlayerId().toString();
//            distributePot(Long.parseLong(winnerId), gameId);
//            setPlayerResults(gameId, winnerId, currentPot);
//            endGame(gameId, winnerId);
//            return; // Exit early since the game is over
//        }
//
//        // Evaluate hands for the remaining players
//        String winnerId = evaluateHands(gameId);
//
//        // If no winner was found, log and handle the situation
//        if (winnerId == null) {
//            System.out.println("No winner could be determined for game ID: " + gameId);
//            return; // or throw an exception if you need stricter error handling
//        }
//
//        BigDecimal winningAmount = currentPot;
//        distributePot(Long.parseLong(winnerId), gameId);
//        setPlayerResults(gameId, winnerId, winningAmount);
//
//        // Check if the game is over again after distributing the pot
//        if (isGameOver(gameId)) {
//            cleanupGameState(gameId);
//            String finalWinnerId = determineFinalWinner(gameId);
//            endGame(gameId, finalWinnerId);
//        } else {
//            resetRoundState();
//            moveToNextPlayer(gameId);
//        }
//
//        // Broadcast event with the winner and the pot amount
//        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
//    }

    private void finishBettingRound1(Long gameId) {
        System.out.println("Finishing betting round for game ID: " + gameId);

        // Check if the betting round is over before proceeding
        if (!isBettingRoundOver(gameId)) {
            System.out.println("Betting round is not over yet.");
            return; // Exit if betting is not complete
        }

        // Retrieve remaining players who have not folded
        List<GamePlayer> remainingPlayers = gamePlayerRepository.findByGameIdAndGameCurrentStatusNot(gameId, GameCurrentStatus.fold);

        // If only one player remains, they are the winner
        if (remainingPlayers.size() == 1) {
            String winnerId = remainingPlayers.get(0).getPlayerId().toString();
            distributePot(Long.parseLong(winnerId), gameId);
            setPlayerResults(gameId, winnerId, currentPot);
            endGame(gameId, winnerId);
            return; // Exit early since the game is over
        }

        // If there are multiple remaining players, evaluate their hands
        String winnerId = evaluateHands(gameId);

        // Check if a winner was found
        if (winnerId == null) {
            System.out.println("No winner could be determined for game ID: " + gameId);
            return; // or throw an exception if you need stricter error handling
        }

        BigDecimal winningAmount = currentPot;
        distributePot(Long.parseLong(winnerId), gameId);
        setPlayerResults(gameId, winnerId, winningAmount);

        // Check if the game is over after distributing the pot
        if (isGameOver(gameId)) {
            cleanupGameState(gameId);
            String finalWinnerId = determineFinalWinner(gameId);
            endGame(gameId, finalWinnerId);
        } else {
            resetRoundState();
            moveToNextPlayer(gameId);
        }

        // Broadcast event with the winner and the pot amount
        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
    }




    public String evaluateHands1(Long gameId) {
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
        Map<String, String> playerResults = new HashMap<>();

        for (GamePlayer gamePlayer : playersInGame) {
            if (gamePlayer.getGameCurrentStatus() != GameCurrentStatus.fold) {
                List<Card> hand = convertStringToHand(Arrays.asList(gamePlayer.getCards().split(",")));
                String handType = HandEvaluator.evaluateHand(hand);
                playerResults.put(gamePlayer.getPlayerId().toString(), handType);
            }
        }

        if (playerResults.isEmpty()) {
            return null; // No valid hands to evaluate
        }

        return determineBestHand(playerResults);
    }


    private void distributePot(Long winnerId, Long gameId) {
        // Fetch the winning player
        GamePlayer winner = gamePlayerRepository.findByPlayerId(winnerId)
                .orElseThrow(() -> new RuntimeException("Winner not found"));

        // Update the winner's total available amount
        Player player = playerRepository.findById(winner.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Add the pot to the winner's total available amount
        player.setTotalAvailableAmount(player.getTotalAvailableAmount().add(currentPot));
        playerRepository.save(player);

        // Reset the current pot after distribution
        currentPot = BigDecimal.ZERO;

        // Log or broadcast the event
        broadcastEvent("POT_DISTRIBUTED", Map.of("winnerId", winnerId, "potAmount", currentPot));
    }


    private void cleanupGameState(Long gameId) {
        currentPot = BigDecimal.ZERO;

        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
        playersInGame.forEach(player -> {
            player.setPlayerBettingAmount(BigDecimal.ZERO);
            player.setGameStatus(GameStatus.active);
            player.setResult(null);
            player.setCards(null);
            gamePlayerRepository.save(player);
        });

        currentPlayerIndex = 0; // Reset current player index
    }

    private String determineFinalWinner(Long gameId) {
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
        return playersInGame.stream()
                .filter(player -> player.getResult() == PlayerGameResult.win)
                .max(Comparator.comparing(GamePlayer::getPlayerBettingAmount))
                .map(player -> player.getPlayerId().toString())
                .orElse(null);
    }

    public String evaluateHands(Long gameId) {
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
        Map<String, String> playerResults = new HashMap<>();

        playersInGame.forEach(gamePlayer -> {
            if (gamePlayer.getGameCurrentStatus() != GameCurrentStatus.fold) {
                List<Card> hand = convertStringToHand(Arrays.asList(gamePlayer.getCards().split(",")));
                String handType = HandEvaluator.evaluateHand(hand);
                playerResults.put(gamePlayer.getPlayerId().toString(), handType);
            }
        });

        return determineBestHand(playerResults);
    }

    private void setPlayerResults(Long gameId, String winnerId, BigDecimal winningAmount) {
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
        GamePlayer winningPlayer = null;

        for (GamePlayer gamePlayer : playersInGame) {
            if (gamePlayer.getPlayerId().toString().equals(winnerId)) {
                gamePlayer.setResult(PlayerGameResult.win);
                winningPlayer = gamePlayer;
            } else {
                gamePlayer.setResult(PlayerGameResult.lose);
            }
            gamePlayerRepository.save(gamePlayer);
        }

        if (winningPlayer != null) {
            saveGameResult(gameId, winningPlayer, winningAmount);
        }
    }

    
    private void saveGameResult(Long gameId, GamePlayer winner, BigDecimal winningAmount) {
        GameResult gameResult = new GameResult();
        gameResult.setGameId(gameId);
        gameResult.setWinningPlayerId(winner.getPlayerId().intValue());
        gameResult.setWinningAmount(winningAmount);
        gameResult.setCreatedAt(LocalDateTime.now());
        gameResult.setCreatedBy("ADMIN");
        gameResultRepository.save(gameResult);
    }

    public boolean isGameOver(Long gameId) {
        List<GamePlayer> activePlayers = gamePlayerRepository.findByGameIdAndGameCurrentStatus(gameId, GameCurrentStatus.fold);
        return activePlayers.size() <= 1 ||
                gamePlayerRepository.findByGameIdAndPlayerBettingAmountGreaterThan(gameId, BigDecimal.ZERO).size() <= 1;
    }

    public void endGame(Long gameId, String finalWinnerId) {
        GameInstance gameInstance = getGameInstance(gameId);
        gameInstance.setGameStatus(GameInstance.GameStatus.completed);
        gameInstanceRepository.save(gameInstance);

        distributePot(Long.parseLong(finalWinnerId), gameId);
        broadcastEvent("GAME_ENDED", Map.of("gameId", gameId, "winnerId", finalWinnerId, "finalPot", currentPot));

        cleanupGameState(gameId);
        System.out.println("Game ID " + gameId + " has ended. Final winner: Player " + finalWinnerId);
    }

    private void resetRoundState() {
        System.out.println("Resetting round state. Current pot: " + currentPot + " | Current bet: " + currentBet);
        currentBet = BigDecimal.ZERO;
        currentPot = BigDecimal.ZERO;
    }

    private void moveToNextPlayer(Long gameId) {
        GameInstance gameInstance = getGameInstance(gameId);
        List<GamePlayer> players = gamePlayerRepository.findByGameId(gameId);

        if (!players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            gameInstance.setCurrentPlayerIndex(currentPlayerIndex);
            gameInstanceRepository.save(gameInstance);

            broadcastEvent("NEXT_PLAYER_TURN", "Player " + players.get(currentPlayerIndex).getPlayerId() + "'s turn.");
        }
    }

    private List<Card> convertStringToHand(List<String> handString) {
        List<Card> hand = new ArrayList<>();
        for (String cardString : handString) {
            String[] parts = cardString.split(" of ");
            hand.add(new Card(parts[1].trim(), parts[0].trim())); // Create Card object with suit and rank
        }
        return hand;
    }

    private String determineBestHand(Map<String, String> playerResults) {
        List<String> handHierarchy = Arrays.asList("HIGH CARD", "PAIR", "COLOR", "SEQUENCE", "PURE SEQUENCE", "TRAIL");
        return playerResults.entrySet().stream()
                .max(Comparator.comparing(entry -> handHierarchy.indexOf(entry.getValue())))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

//    private boolean isBettingRoundOver(Long gameId) {
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//        return playersInGame.stream().allMatch(player -> {
//            if (player.getGameCurrentStatus() == GameCurrentStatus.fold) return true;
//            return player.getPlayerBettingAmount().compareTo(currentBet) >= 0;
//        });
//    }
//    private boolean isBettingRoundOver(Long gameId) {
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//        long activePlayersCount = playersInGame.stream()
//                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
//                .count();
//
//        // If there are no active players, the betting round is over
//        if (activePlayersCount == 0) {
//            return true;
//        }
//
//        // Check if all active players have matched or folded
//        return playersInGame.stream().allMatch(player -> {
//            // Players who have folded are already counted
//            if (player.getGameCurrentStatus() == GameCurrentStatus.fold) return true;
//            return player.getPlayerBettingAmount().compareTo(currentBet) >= 0;
//        });
//    }


    // Method to check if the betting round is over
//    private boolean isBettingRoundOver(Long gameId) {
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        // Count active players who have not folded
//        long activePlayersCount = playersInGame.stream()
//                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
//                .count();
//
//        // If there are no active players, the betting round is over
//        if (activePlayersCount == 0) {
//            return true;
//        }
//
//        // Check if all active players have matched or folded
//        boolean allPlayersMatched = playersInGame.stream().allMatch(player -> {
//            // Players who have folded are already counted
//            if (player.getGameCurrentStatus() == GameCurrentStatus.fold) return true;
//            return player.getPlayerBettingAmount().compareTo(currentBet) >= 0;
//        });
//
//        System.out.println("Is betting round over? " + allPlayersMatched);
//        return allPlayersMatched;
//    }
    private boolean isBettingRoundOver(Long gameId) {
        // Retrieve players in the current game
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);

        // Check if all players have either folded or matched the current bet
        boolean allPlayersMatched = playersInGame.stream().allMatch(player ->
                player.getGameCurrentStatus() == GameCurrentStatus.fold ||
                        player.getPlayerBettingAmount().compareTo(currentBet) >= 0
        );

        System.out.println("Is betting round over? " + allPlayersMatched);
        return allPlayersMatched;
    }


    private void finishBettingRound(Long gameId) {
        System.out.println("Finishing betting round for game ID: " + gameId);

        // Check if the betting round is over before proceeding
        if (!isBettingRoundOver(gameId)) {
            System.out.println("Betting round is not over yet.");
            return; // Exit if betting is not complete
        }

        // Evaluate hands for all remaining players
        String winnerId = evaluateHands(gameId);
        System.out.println("Winner for the round: Player " + winnerId);

        // If no winner was found, log and handle the situation
        if (winnerId == null) {
            System.out.println("No winner could be determined for game ID: " + gameId);
            return; // or throw an exception if you need stricter error handling
        }

        // Distribute pot and set player results
        distributePot(Long.parseLong(winnerId), gameId);
        setPlayerResults(gameId, winnerId, currentPot); // Assuming currentPot is the winning amount
        System.out.println("Winner for this round: Player " + winnerId + " with pot amount: " + currentPot);

        // Check if the game is over after distributing the pot
        if (isGameOver(gameId)) {
            cleanupGameState(gameId);
            String finalWinnerId = determineFinalWinner(gameId);
            endGame(gameId, finalWinnerId);
            System.out.println("Final winner after the game ended: " + finalWinnerId);
        } else {
            resetRoundState();
            moveToNextPlayer(gameId);
        }

        // Broadcast event with the winner and the pot amount
        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
    }


    // Method to finish the betting round
//    private void finishBettingRound(Long gameId) {
//        System.out.println("Finishing betting round for game ID: " + gameId);
//
//        // Check if the betting round is over before proceeding
//        if (!isBettingRoundOver(gameId)) {
//            System.out.println("Betting round is not over yet.");
//            return; // Exit if betting is not complete
//        }
//
//        // Retrieve remaining players who have not folded
//        List<GamePlayer> remainingPlayers = gamePlayerRepository.findByGameIdAndGameCurrentStatusNot(gameId, GameCurrentStatus.fold);
//
//        // If only one player remains, they are the winner
//        if (remainingPlayers.size() == 1) {
//            String winnerId = remainingPlayers.get(0).getPlayerId().toString();
//            distributePot(Long.parseLong(winnerId), gameId);
//            setPlayerResults(gameId, winnerId, currentPot);
//            endGame(gameId, winnerId);
//            System.out.println("Winner is the last remaining player: " + winnerId);
//            return; // Exit early since the game is over
//        }
//
//        // If there are multiple remaining players, evaluate their hands
//        String winnerId = evaluateHands(gameId);
//
//        // Check if a winner was found
//            if (winnerId == null) {
//            System.out.println("No winner could be determined for game ID: " + gameId);
//            return; // or throw an exception if you need stricter error handling
//        }
//
//        BigDecimal winningAmount = currentPot;
//        distributePot(Long.parseLong(winnerId), gameId);
//        setPlayerResults(gameId, winnerId, winningAmount);
//        System.out.println("Winner for this round: Player " + winnerId + " with pot amount: " + winningAmount);
//
//        // Check if the game is over after distributing the pot
//        if (isGameOver(gameId)) {
//            cleanupGameState(gameId);
//            String finalWinnerId = determineFinalWinner(gameId);
//            endGame(gameId, finalWinnerId);
//            System.out.println("Final winner after the game ended: " + finalWinnerId);
//        } else {
//            resetRoundState();
//            moveToNextPlayer(gameId);
//        }
//
//        // Broadcast event with the winner and the pot amount
//        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
//    }




    private void processBet(GamePlayer gamePlayer, BigDecimal amount) {
        Player player = playerRepository.findById(gamePlayer.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        player.setTotalAvailableAmount(player.getTotalAvailableAmount().subtract(amount));
        playerRepository.save(player);

        currentBet = currentBet.max(amount);
        currentPot = currentPot.add(amount);

        gamePlayer.setPlayerBettingAmount(gamePlayer.getPlayerBettingAmount().add(amount));
        gamePlayerRepository.save(gamePlayer);
    }



    private void validateCurrentPlayer(Long playerId, Long gameId) {
        Long currentPlayerId = turnManager.getCurrentPlayerId(gameId);
        if (!currentPlayerId.equals(playerId)) {
            throw new IllegalStateException("It's not your turn!");
        }
    }

    private void validatePlayerChips(Long playerId, BigDecimal betAmount) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (player.getTotalAvailableAmount().compareTo(betAmount) < 0) {
            throw new IllegalStateException("Not enough chips to place the bet.");
        }
    }

    private void validateFirstBet(BigDecimal betAmount) {
        if (currentBet.compareTo(BigDecimal.ZERO) == 0 && betAmount.compareTo(ANTE_AMOUNT) < 0) {
            throw new IllegalStateException("First bet must be at least the ante amount.");
        }
    }

    private void validateBet(GamePlayer gamePlayer, BigDecimal betAmount) {
        if (betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be greater than zero.");
        }
        if (gamePlayer.getGameCurrentStatus() == GameCurrentStatus.fold) {
            throw new IllegalStateException("Cannot place a bet when folded.");
        }
    }

    private void broadcastEvent(String eventType, Object payload) {
        // Placeholder for broadcasting events to clients
        System.out.println("Broadcasting Event: " + eventType + " | Payload: " + payload);
    }

}
//
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
//    private TurnManager turnManager;
//
//    private static final int ANTE_AMOUNT = 100;
//
//    private int currentPot = 0;
//    private int currentBet = 0;
//
//    public void placeBet(Long playerId, int amount, Long gameId, boolean isBlind) {
//        System.out.println("Attempting to place a bet...");
//        System.out.println("Player ID: " + playerId + " | Game ID: " + gameId + " | Bet Amount: " + amount + " | Blind: " + isBlind);
//
//        // Fetch game and validate
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        if (game.getStatus() != GameStatus.ACTIVE) {
//            throw new IllegalStateException("Game is not active.");
//        }
//
//        // Fetch player in game
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//        // Validate current player
//        validateCurrentPlayer(playerId, gameId);
//
//        // Ensure the player has enough chips
//        validatePlayerChips(playerId, amount);
//
//        // Ensure the first bet is at least the ANTE_AMOUNT
//        validateFirstBet(amount);
//
//        // Validate and process the bet
//        validateBet(playerGame, amount, isBlind);
//        processBet(playerGame, amount);
//
//        // Broadcast the betting event and manage turn
//        broadcastEvent("BET_PLACED", Map.of("playerId", playerId, "amount", amount));
//        moveToNextPlayer(gameId);
//
//        // Check if the round is over
//        if (isBettingRoundOver(gameId)) {
//            finishBettingRound(gameId);
//        } else {
//            System.out.println("Betting round continues for game ID: " + gameId);
//        }
//    }
//
//    private void validateCurrentPlayer(Long playerId, Long gameId) {
//        Long currentPlayerId = turnManager.getCurrentPlayerId(gameId);
//        if (currentPlayerId == null || !currentPlayerId.equals(playerId)) {
//            System.out.println("It's not the player's turn. Current player ID: " + currentPlayerId);
//            throw new IllegalStateException("It's not your turn!");
//        }
//    }
//
//    private void validatePlayerChips(Long playerId, int amount) {
//        Player player = playerRepository.findById(playerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//        if (player.getChips() < amount) {
//            System.out.println("Player " + playerId + " does not have enough chips to place the bet.");
//            throw new IllegalArgumentException("Insufficient chips to place the bet");
//        }
//    }
//
//    private void validateFirstBet(int amount) {
//        if (currentBet == 0 && amount < ANTE_AMOUNT) {
//            System.out.println("First bet is below the ANTE_AMOUNT. Current bet: " + amount + " | ANTE_AMOUNT: " + ANTE_AMOUNT);
//            throw new IllegalArgumentException("The first bet must be at least " + ANTE_AMOUNT);
//        }
//    }
//
//    private void validateBet(PlayerGame playerGame, int amount, boolean isBlind) {
//        int minBet = currentBet == 0 ? ANTE_AMOUNT : currentBet;
//        int maxBet = minBet * 2;
//
//        if (isBlind) {
//            if (amount < minBet || amount > maxBet) {
//                System.out.println("Invalid blind bet. Amount: " + amount + " | MinBet: " + minBet + " | MaxBet: " + maxBet);
//                throw new IllegalArgumentException("Blind bet must be between " + minBet + " and " + maxBet);
//            }
//        } else {
//            if (amount < minBet * 2) {
//                System.out.println("Invalid seen player bet. Amount: " + amount + " | MinBet for seen players: " + (minBet * 2));
//                throw new IllegalArgumentException("Seen players must bet at least " + (minBet * 2));
//            }
//        }
//
//        System.out.println("Bet validated. Amount: " + amount + " | Blind: " + isBlind);
//    }
//
//    private void processBet(PlayerGame playerGame, int amount) {
//        // Subtract the bet amount from the player's available chips
//        Player player = playerRepository.findById(playerGame.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
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
//        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
//        resetRoundState();
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
//        for (PlayerGame playerGame : playersInGame) {
//            playerGame.setResult(playerGame.getUserId().toString().equals(winnerId) ? GameResult.WON : GameResult.LOST);
//            playerGameRepository.save(playerGame);
//        }
//    }
//
//    private void resetRoundState() {
//        System.out.println("Resetting round state. Current pot: " + currentPot + " | Current bet: " + currentBet);
//        currentBet = 0;
//        currentPot = 0;
//    }
//
//
//
//    private void moveToNextPlayer(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        turnManager.moveToNextPlayer(gameId);
//    }
//
//    private void endGame(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//        game.setStatus(GameStatus.COMPLETED);
//        gameRepository.save(game);
//        System.out.println("Game ID " + gameId + " ended.");
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
//    private void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}