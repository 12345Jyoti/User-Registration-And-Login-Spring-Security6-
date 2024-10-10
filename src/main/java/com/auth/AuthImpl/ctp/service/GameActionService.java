//package com.auth.AuthImpl.ctp.service;
//
//import com.auth.AuthImpl.ctp.actionImpl.TurnManager;
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.enums.*;
//import com.auth.AuthImpl.ctp.model.Card;
//import com.auth.AuthImpl.ctp.nenity.*;
//import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
//import com.auth.AuthImpl.ctp.repository.GamePlayerRepository;
//import com.auth.AuthImpl.ctp.repository.GameResultRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.ctp.util.CardUtils;
//import com.auth.AuthImpl.registraion.enums.Status;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.stream.Collectors;
//
//@Service
//public class GameActionService  {
//
//    @Autowired
//    private GameInstanceRepository gameInstanceRepository;
//
//    @Autowired
//    private GamePlayerRepository gamePlayerRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    private TurnManager turnManager;
//
//    private int currentPlayerIndex;  // Track the current player's index
//
//    @Autowired
//    private GameResultRepository gameResultRepository;
//
//    private List<String> deck = new ArrayList<>();
//
//    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    private GameInstance currentGameInstance;
//    private static final int ANTE_AMOUNT = 100;
//    private BigDecimal currentPot;
//    private BigDecimal currentBet;
//
//
//
//    public void placeBet(Long playerId, BigDecimal betAmount) {
//        System.out.println("Attempting to place a bet...");
//        System.out.println("Player ID: " + playerId + " | Bet Amount: " + betAmount);
//
//        // Fetch the GamePlayer object
//        GamePlayer gamePlayer = gamePlayerRepository.findByPlayerId(playerId)
//                .orElseThrow(() -> new RuntimeException("Player not found"));
//
//        // Fetch the Game to validate the state
//        GameInstance game = gameInstanceRepository.findById(gamePlayer.getGameId())
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        // Validate the game status
//        if (game.getStatus() != Status.LIVE) {
//            throw new IllegalStateException("Game is not active.");
//        }
//
//        // Validate current player turn
//        validateCurrentPlayer(playerId, gamePlayer.getGameId());
//
//        // Validate the player's available chips
//        validatePlayerChips(playerId, betAmount);
//
//        // Validate the first bet if necessary (assuming ANTE_AMOUNT is defined)
//        validateFirstBet(betAmount);
//
//        // Validate the bet amount
//        validateBet(gamePlayer, betAmount);
//
//        // Process the bet
//        processBet(gamePlayer, betAmount);
//
//        // Broadcast the betting event
//        broadcastEvent("BET_PLACED", "Player " + playerId + " has placed a bet of " + betAmount);
//
//        // Move to the next player
////        moveToNextPlayer(gamePlayer.getGameId());
//
//        // Check if the betting round is over
//        if (isBettingRoundOver(gamePlayer.getGameId())) {
//            finishBettingRound(gamePlayer.getGameId());
//        } else {
//            System.out.println("Betting round continues for game ID: " + gamePlayer.getGameId());
//        }
//    }
//
//    public void finishBettingRound(Long gameId) {
//        System.out.println("Finishing betting round for game ID: " + gameId);
//
//        // Determine the winner of the round
//        String winnerId = evaluateHands(gameId);  // Implemented to determine the round winner.
//        System.out.println("Winner for the round: Player " + winnerId);
//
//        // Distribute the pot to the winner
//        distributePot(Long.parseLong(winnerId), gameId);
//
//        // Calculate the winning amount (assumed currentPot is the total pot amount)
//        BigDecimal winningAmount = currentPot;  // You may need to calculate this based on your logic.
//
//        // Set the results of the round for each player, including the winning amount
//        setPlayerResults(gameId, winnerId, winningAmount);
//
//        // Check if the game is over
//        if (isGameOver(gameId)) {
//            // Cleanup any game state before determining the final winner
//            cleanupGameState(gameId);
//
//            // Determine the final winner of the game
//            String finalWinnerId = determineFinalWinner(gameId);
//            System.out.println("Final winner for the game: Player " + finalWinnerId);
//
//            // End the game
//            endGame(gameId);
//        } else {
//            resetRoundState();
//            moveToNextPlayer(gameId);  // Move to the next player's turn
//        }
//
//        // Broadcast the event of round finishing
//        broadcastEvent("ROUND_FINISHED", Map.of("winnerId", winnerId, "potAmount", currentPot));
//    }
//
//    private void cleanupGameState(Long gameId) {
//        // Reset the current pot
//        currentPot = BigDecimal.ZERO;
//
//        // Fetch all players participating in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        // Reset player states for the next round or game
//        for (GamePlayer player : playersInGame) {
//            player.setPlayerBettingAmount(BigDecimal.ZERO);  // Reset current bet
//            player.setGameStatus(GameStatus.active);  // Reset status to active
//            player.setResult(null);  // Clear game result for the next round
//            player.setCards(null);  // Clear cards for the next round if necessary
//
//            // Save the updated player state
//            gamePlayerRepository.save(player);
//        }
//
//        // Reset the current player index to the first player
//        currentPlayerIndex = 0;
//    }
//
//    private String determineFinalWinner(Long gameId) {
//        // Fetch all players participating in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        GamePlayer finalWinner = null;
//        BigDecimal highestWinningAmount = BigDecimal.ZERO;
//
//        // Loop through each player to determine the final winner
//        for (GamePlayer player : playersInGame) {
//            // You can have various criteria for determining the final winner.
//            // Assuming the winning player has a WON status in GameResult.
//            if (player.getResult() == PlayerGameResult.win) {
//                // Assuming you have a method to get the total amount won by the player
//                BigDecimal totalWonByPlayer = player.getPlayerBettingAmount(); // Replace this with your logic to calculate total winnings
//
//                // Check if this player has the highest winning amount
//                if (totalWonByPlayer.compareTo(highestWinningAmount) > 0) {
//                    highestWinningAmount = totalWonByPlayer;
//                    finalWinner = player;
//                }
//            }
//        }
//
//        if (finalWinner != null) {
//            // Return the ID of the winning player
//            return finalWinner.getPlayerId().toString();
//        } else {
//            // Handle the case where there is no winner (for example, if all players lost)
//            return null; // Or return some indication of no winner
//        }
//    }
//
//
//    // Method to evaluate the hands of all active players and determine the winner for the round
//    public String evaluateHands(Long gameId) {
//        // Fetch all players in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        // A map to store playerId and their corresponding hand strength (handType)
//        Map<String, String> playerResults = new HashMap<>();
//
//        // Loop through each player to evaluate their hands
//        for (GamePlayer gamePlayer : playersInGame) {
//            // Only evaluate hands for players who haven't folded
//            if (gamePlayer.getGameCurrentStatus() != GameCurrentStatus.fold) {
//                // Convert player's cards from string to list of Card objects
//                List<Card> hand = convertStringToHand(Arrays.asList(gamePlayer.getCards().split(",")));
//
//                // Evaluate the hand using a HandEvaluator (placeholder for hand ranking logic)
//                String handType = HandEvaluator.evaluateHand(hand);  // Example handType: "PAIR", "TRAIL", etc.
//
//                // Store the player's evaluated hand result
//                playerResults.put(gamePlayer.getPlayerId().toString(), handType);
//            }
//        }
//
//        // Determine the best hand among the players
//        return determineBestHand(playerResults);
//    }
//
//    // Method to set the player results (win/loss) after the round is evaluated
//    private void setPlayerResults(Long gameId, String winnerId, BigDecimal winningAmount) {
//        // Fetch all players participating in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        // Track the winning player
//        GamePlayer winningPlayer = null;
//
//        // Loop through each player and set the result based on whether they are the winner or not
//        for (GamePlayer gamePlayer : playersInGame) {
//            if (gamePlayer.getPlayerId().toString().equals(winnerId)) {
//                gamePlayer.setResult(PlayerGameResult.win);  // Player won
//                winningPlayer = gamePlayer;  // Set as the winning player
//            } else {
//                gamePlayer.setResult(PlayerGameResult.lose  );  // Other players lost
//            }
//
//            // Save the updated player result
//            gamePlayerRepository.save(gamePlayer);
//        }
//
//        // If there is a winning player, create a new GameResult entry for the game
//        if (winningPlayer != null) {
//            GameResult gameResult = new GameResult();
//            gameResult.setGameId(gameId.intValue());
//            gameResult.setWinningPlayerId(winningPlayer.getPlayerId().intValue());
//            gameResult.setWinningAmount(winningAmount);  // Assuming the winning amount is passed in
//            gameResult.setCreatedBy("Admin");
//
//            // Set the date of the result to the current time
//            gameResult.setCreatedAt(LocalDateTime.now());
//
//            // Save the game result in the database
//            gameResultRepository.save(gameResult);
//        }
//    }
//
//
//    // Method to check if the game is over (e.g., only one player remains or other conditions)
//    public boolean isGameOver(Long gameId) {
//        // Fetch the list of active players (those who haven't folded)
//        List<GamePlayer> activePlayers = gamePlayerRepository.findByGameIdAndGameCurrentStatus(gameId, GameCurrentStatus.fold);
//
//        // If only one player remains, the game is over
//        if (activePlayers.size() == 1) {
//            System.out.println("Game over. Only one player remaining.");
//            return true;
//        }
//
//        // Additional game-over conditions (e.g., no chips left, max rounds reached)
//        List<GamePlayer> playersWithChips = gamePlayerRepository.findByGameIdAndPlayerBettingAmountGreaterThan(gameId, BigDecimal.ZERO);
//        if (playersWithChips.size() == 1) {
//            System.out.println("Game over. Only one player has chips remaining.");
//            return true;
//        }
//
//        // Game is not over yet
//        return false;
//    }
//
//    // Method to end the game when the game is over
//    public void endGame(Long gameId) {
//        // Fetch the game instance
//        GameInstance gameInstance = gameInstanceRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        // Update the game status to completed
//        gameInstance.setGameStatus(GameInstance.GameStatus.completed);
//        gameInstanceRepository.save(gameInstance);
//
//        // Determine the final winner
//        String finalWinnerId = determineFinalWinner(gameId);
//        System.out.println("Final winner of the game: Player " + finalWinnerId);
//
//        // Distribute any remaining pot to the winner
//        if (currentPot.compareTo(BigDecimal.ZERO) > 0) {
//            distributePot(Long.parseLong(finalWinnerId), gameId);
//        }
//
//        // Broadcast that the game has ended
//        broadcastEvent("GAME_ENDED", Map.of("gameId", gameId, "winnerId", finalWinnerId, "finalPot", currentPot));
//
//        // Clean up game state
//        cleanupGameState(gameId);
//
//        // Log the game ending
//        System.out.println("Game ID " + gameId + " has ended.");
//    }
//
//    // Method to reset the round state for the next betting round
//    private void resetRoundState() {
//        System.out.println("Resetting round state. Current pot: " + currentPot + " | Current bet: " + currentBet);
//        currentBet = BigDecimal.ZERO;  // Reset the current bet to zero
//        currentPot = BigDecimal.ZERO;  // Reset the current pot to zero
//    }
//
//    // Method to move to the next player in the round
//    private void moveToNextPlayer(Long gameId) {
//        // Retrieve the game instance
//        GameInstance gameInstance = gameInstanceRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        // Fetch all players in the game
//        List<GamePlayer> players = gamePlayerRepository.findByGameId(gameId);
//
//        if (!players.isEmpty()) {
//            // Get the current player index
//            Integer currentPlayerIndex = gameInstance.getCurrentPlayerIndex();
//
//            // Calculate the next player's turn
//            int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
//
//            // Set the new current player
//            gameInstance.setPrevPlayerIndex(currentPlayerIndex);  // Set previous player
//            gameInstance.setCurrentPlayerIndex(nextPlayerIndex);  // Set the new current player
//            gameInstance.setNextPlayerIndex((nextPlayerIndex + 1) % players.size());  // Set the next player
//
//            // Save the updated game instance
//            gameInstanceRepository.save(gameInstance);
//
//            // Inform the system about the next player's turn
//            broadcastEvent("NEXT_PLAYER_TURN", "Player " + players.get(nextPlayerIndex).getPlayerId() + "'s turn.");
//        }
//    }
//
//// Additional helper methods
//
//    // Method to convert a string of cards to a list of Card objects
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
//    // Method to determine the best hand based on hand hierarchy
//    private String determineBestHand(Map<String, String> playerResults) {
//        // Define hand hierarchy from weakest to strongest (e.g., "HIGH CARD" < "PAIR" < "TRAIL")
//        List<String> handHierarchy = Arrays.asList("HIGH CARD", "PAIR", "COLOR", "SEQUENCE", "PURE SEQUENCE", "TRAIL");
//
//        String bestPlayerId = null;
//        String bestHandType = null;
//
//        // Iterate through the players' results and find the one with the highest-ranked hand
//        for (Map.Entry<String, String> entry : playerResults.entrySet()) {
//            String playerId = entry.getKey();
//            String handType = entry.getValue();
//
//            if (bestHandType == null || handHierarchy.indexOf(handType) > handHierarchy.indexOf(bestHandType)) {
//                bestHandType = handType;
//                bestPlayerId = playerId;
//            }
//        }
//
//        // Return the player ID of the best hand
//        return bestPlayerId;
//    }
//
//
//    private boolean isBettingRoundOver(Long gameId) {
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        boolean allPlayersMatched = playersInGame.stream().allMatch(playerGame -> {
//            if (playerGame.getGameCurrentStatus() == GameCurrentStatus.fold) {
//                return true;  // Player has folded, no further action needed
//            }
//            if (playerGame.getGameCurrentStatus() == GameCurrentStatus.blind) {
//                // Blind player must match or exceed the current bet
//                return playerGame.getPlayerBettingAmount().compareTo(currentBet) >= 0;
//            } else if (playerGame.getGameCurrentStatus() == GameCurrentStatus.seen) {
//                // Seen player must match or exceed the current bet
//                return playerGame.getPlayerBettingAmount().compareTo(currentBet) >= 0;
//            }
//            return false;
//        });
//
//        System.out.println("Is betting round over? " + allPlayersMatched);
//        return allPlayersMatched;
//    }
//
//    private void processBet(GamePlayer gamePlayer, BigDecimal amount) {
//        // Ensure currentPot is not null; if it is, initialize it to BigDecimal.ZERO
//        if (currentPot == null) {
//            currentPot = BigDecimal.ZERO;
//        }
//
//        // Ensure currentBet is not null; if it is, initialize it to BigDecimal.ZERO
//        if (currentBet == null) {
//            currentBet = BigDecimal.ZERO;
//        }
//
//        // Subtract the bet amount from the player's available chips
//        Player player = playerRepository.findById(gamePlayer.getPlayerId())
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//        player.setTotalAvailableAmount(player.getTotalAvailableAmount().subtract(amount));
//        playerRepository.save(player);
//
//        // Update the current bet and pot
//        currentBet = currentBet.max(amount);  // Compare and get the max of currentBet and amount
//        currentPot = currentPot.add(amount);  // Add the bet amount to the current pot
//
//        System.out.println("Processed bet for player " + player.getId() + ". Bet Amount: " + amount + " | Current Pot: " + currentPot + " | Current Bet: " + currentBet);
//
//        // Update the player's bet amount
//        gamePlayer.setPlayerBettingAmount(gamePlayer.getPlayerBettingAmount().add(amount));
//        gamePlayerRepository.save(gamePlayer);
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
//    private void validatePlayerChips(Long playerId, BigDecimal amount) {
//        Player player = playerRepository.findById(playerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//        if (player.getTotalAvailableAmount().compareTo(amount) < 0) {
//            System.out.println("Player " + playerId + " does not have enough chips to place the bet.");
//            throw new IllegalArgumentException("Insufficient chips to place the bet");
//        }
//    }
//
//private void validateFirstBet(BigDecimal amount) {
//    // Ensure currentBet is not null; if it is, treat it as zero
//    if (currentBet == null) {
//        currentBet = BigDecimal.ZERO;
//    }
//
//    // Now perform the comparison safely
//    if (currentBet.equals(BigDecimal.ZERO) && amount.compareTo(BigDecimal.valueOf(ANTE_AMOUNT)) < 0) {
//        System.out.println("First bet is below the ANTE_AMOUNT. Current bet: " + amount + " | ANTE_AMOUNT: " + ANTE_AMOUNT);
//        throw new IllegalArgumentException("The first bet must be at least " + ANTE_AMOUNT);
//    }
//}
//
//
//    private void validateBet(GamePlayer gamePlayer, BigDecimal amount) {
//        BigDecimal minBet = currentBet.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.valueOf(ANTE_AMOUNT) : currentBet;
//        BigDecimal maxBet = minBet.multiply(BigDecimal.valueOf(2));
//
//        if (amount.compareTo(minBet) < 0 || amount.compareTo(maxBet) > 0) {
//            System.out.println("Invalid bet. Amount: " + amount + " | MinBet: " + minBet + " | MaxBet: " + maxBet);
//            throw new IllegalArgumentException("Bet must be between " + minBet + " and " + maxBet);
//        }
//
//        System.out.println("Bet validated. Amount: " + amount);
//    }
//
//    // Distribute pot to the winner
//    public void distributePot(Long winnerId, Long gameId) {
//        GamePlayer winnerGamePlayer = gamePlayerRepository.findByPlayerId(winnerId)
//                .orElseThrow(() -> new IllegalArgumentException("Winner not found in the game"));
//
//        Player winner = playerRepository.findById(winnerId)
//                .orElseThrow(() -> new IllegalArgumentException("Winner's player profile not found"));
//
//        // Add the current pot amount to the winner's chips
//        winner.setTotalAvailableAmount(winner.getTotalAvailableAmount().add(currentPot));
//
//        playerRepository.save(winner);
//
//        System.out.println("Distributed pot of " + currentPot + " to player " + winnerId);
//
//        // Reset pot after distribution
//        currentPot = BigDecimal.ZERO;
//    }
//
//
//    public void fold(Long playerId) {
//        // Fetch the player who is folding
//        GamePlayer gamePlayer = gamePlayerRepository.findByPlayerId(playerId)
//                .orElseThrow(() -> new RuntimeException("Player not found"));
//
//        // Update the player's status to reflect they have folded
//        gamePlayer.setGameCurrentStatus(GameCurrentStatus.fold);
//        gamePlayerRepository.save(gamePlayer);
//
//        // Broadcast the fold event
//        broadcastEvent("PLAYER_FOLDED", "Player " + playerId + " has folded.");
//
//        // Check if the game should end (e.g., if all players but one have folded)
//        checkIfGameOverAfterFold(gamePlayer.getGameId());
//    }
//
//    private void checkIfGameOverAfterFold(Long gameId) {
//        // Fetch all players participating in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//
//        // Count how many players are still active (haven't folded)
//        long activePlayersCount = playersInGame.stream()
//                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
//                .count();
//
//        // If there is only one active player left, declare them the winner
//        if (activePlayersCount == 1) {
//            GamePlayer winner = playersInGame.stream()
//                    .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("No active players found"));
//
//            // Handle the game result
//            endGameAndSaveResult(gameId, winner);
//        }
//    }
//
//    private void endGameAndSaveResult(Long gameId, GamePlayer winner) {
//        // Declare the game is over and save the result in GameResult table
//        GameResult gameResult = new GameResult();
//        gameResult.setGameId(gameId.intValue());
//        gameResult.setWinningPlayerId(winner.getPlayerId().intValue());
//        gameResult.setWinningAmount(calculateWinningAmount(gameId));
//        gameResult.setCreatedAt(LocalDateTime.now());
//
//        // Save the game result
//        gameResultRepository.save(gameResult);
//
//        // Update the winner's result to WON
//        winner.setResult(PlayerGameResult.win);
//        gamePlayerRepository.save(winner);
//
//        // Mark all other players as LOST
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);
//        playersInGame.stream()
//                .filter(player -> !player.getPlayerId().equals(winner.getPlayerId()))
//                .forEach(player -> {
//                    player.setResult(PlayerGameResult.lose);
//                    gamePlayerRepository.save(player);
//                });
//
//        // Broadcast that the game has ended
//        broadcastEvent("GAME_OVER", "Game " + gameId + " has ended. Player " + winner.getPlayerId() + " is the winner.");
//    }
//
//
//    private BigDecimal calculateWinningAmount(Long gameId) {
//        // Implement your logic to calculate the total winning amount
//        // This could involve summing up bets or other game-related calculations
//        return new BigDecimal("1000");  // Placeholder amount
//    }
//
//    public void sideShow(Long requestingPlayerId, Long defendingPlayerId) {
//        // Fetch the players involved in the side show
//        GamePlayer requestingPlayer = gamePlayerRepository.findByPlayerId(requestingPlayerId)
//                .orElseThrow(() -> new RuntimeException("Requesting player not found"));
//        GamePlayer defendingPlayer = gamePlayerRepository.findByPlayerId(defendingPlayerId)
//                .orElseThrow(() -> new RuntimeException("Defending player not found"));
//
//        // Ensure both players are still active
//        if (requestingPlayer.getGameCurrentStatus() == GameCurrentStatus.fold ||
//                defendingPlayer.getGameCurrentStatus() == GameCurrentStatus.fold) {
//            throw new IllegalStateException("Folded players cannot participate in a side show.");
//        }
//
//        // Evaluate the strength of both players' hands
//        int requestingPlayerHandStrength = evaluateHandStrength(requestingPlayer.getCards());
//        int defendingPlayerHandStrength = evaluateHandStrength(defendingPlayer.getCards());
//
//        // Determine the result of the side show
//        if (requestingPlayerHandStrength > defendingPlayerHandStrength) {
//            // Requesting player wins, defender must fold
//            defendingPlayer.setGameCurrentStatus(GameCurrentStatus.fold);
//            gamePlayerRepository.save(defendingPlayer);
//            broadcastEvent("SIDE_SHOW_RESULT", "Player " + requestingPlayerId + " won the side show. Player " + defendingPlayerId + " has folded.");
//        } else {
//            // Defending player wins, requester must fold
//            requestingPlayer.setGameCurrentStatus(GameCurrentStatus.fold);
//            gamePlayerRepository.save(requestingPlayer);
//            broadcastEvent("SIDE_SHOW_RESULT", "Player " + defendingPlayerId + " won the side show. Player " + requestingPlayerId + " has folded.");
//        }
//
//        // Check if the game should end after the side show
//        checkIfGameOverAfterFold(requestingPlayer.getGameId());
//    }
//
//
//    public void showCards(Long playerId) {
//        // Fetch the player who is showing cards
//        GamePlayer gamePlayer = gamePlayerRepository.findByPlayerId(playerId)
//                .orElseThrow(() -> new RuntimeException("Player not found"));
//
//        // Ensure the player is still active
//        if (gamePlayer.getGameCurrentStatus() == GameCurrentStatus.fold) {
//            throw new IllegalStateException("Folded players cannot show cards.");
//        }
//
//        // Get all players participating in the game
//        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gamePlayer.getGameId());
//
//        // Only active players (not folded) should participate in the card show
//        List<GamePlayer> activePlayers = playersInGame.stream()
//                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
//                .collect(Collectors.toList());
//
//        if (activePlayers.size() < 2) {
//            throw new IllegalStateException("Not enough active players to perform a card show.");
//        }
//
//        // Show the cards for all active players and determine the winner
//        GamePlayer winner = determineWinner(activePlayers);
//
//        // End the game and save the result
//        endGameAndSaveResult(gamePlayer.getGameId(), winner);
//
//        // Broadcast the show card event
//        broadcastEvent("SHOW_CARDS", "Player " + playerId + " has initiated a card show. Player " + winner.getPlayerId() + " wins.");
//    }
//
//
//    private GamePlayer determineWinner(List<GamePlayer> activePlayers) {
//        // Check if there are no active players
//        if (activePlayers.isEmpty()) {
//            return null; // No players left to decide
//        }
//
//        // If there's only one active player, they automatically win
//        if (activePlayers.size() == 1) {
//            GamePlayer winner = activePlayers.get(0);
//            winner.setResult(PlayerGameResult.win); // Set the winner's result
//            announceWinners(Collections.singletonList(winner)); // Announce the single winner
//            return winner; // Return the winner
//        }
//
//        // To store the best hand and potential winners
//        String bestHandType = null;
//        GamePlayer singleWinner = null;
//
//        // Evaluate hands for active players
//        for (GamePlayer player : activePlayers) {
//            // Use CardUtils to parse the cards
//            List<Card> hand = convertStringToHand(Arrays.asList(player.getCards().split(",")));
//            String handType = HandEvaluator.evaluateHand(hand); // Assuming this method returns a hand type
//
//            // Compare the current player's hand with the best hand
//            if (bestHandType == null || HandEvaluator.compareHands(handType, bestHandType) > 0) {
//                bestHandType = handType;
//                singleWinner = player; // Update the current best player
//            } else if (HandEvaluator.compareHands(handType, bestHandType) == 0) {
//                // Handle tie case by setting singleWinner to null
//                singleWinner = null;
//            }
//        }
//
//        // Announce the winner
//        if (singleWinner != null) {
//            singleWinner.setResult(PlayerGameResult.win);
//            announceWinners(Collections.singletonList(singleWinner)); // Announce the single winner
//        } else {
//            System.out.println("No clear winner due to a tie among active players.");
//        }
//
//        return singleWinner; // Return the winning player (or null if no clear winner)
//    }
//
//    private void announceWinners(List<GamePlayer> winners) {
//        if (winners == null || winners.isEmpty()) {
//            System.out.println("No winners to announce.");
//            return;
//        }
//
//        // Create a message to announce the winners
//        StringBuilder message = new StringBuilder("Winners: ");
//
//        for (GamePlayer winner : winners) {
//            message.append("Player ")
//                    .append(winner.getPlayerId())
//                    .append(" with cards: ")
//                    .append(winner.getCards())
//                    .append(", ");
//        }
//
//        // Remove the last comma and space
//        message.setLength(message.length() - 2);
//
//        // Broadcast the announcement (you could also log it or handle it differently)
//        System.out.println(message.toString());
//
//        // Additional logic for broadcasting to other players in the game (if needed)
//        broadcastEvent("WINNERS_ANNOUNCED", message.toString());
//    }
//
//
//
//    private int evaluateHandStrength(String cards) {
//        // Parse the cards from JSON
//        List<Card> playerCards = CardUtils.parseCards(cards);
//
//        // Calculate and return the strength of the hand
//        return CardUtils.calculateHandStrength(playerCards);
//    }
//
//
//    // New method to initialize the deck
//
//    protected void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}
