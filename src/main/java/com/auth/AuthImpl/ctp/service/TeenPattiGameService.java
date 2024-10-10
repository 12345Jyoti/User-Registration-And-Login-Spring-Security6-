//package com.auth.AuthImpl.ctp.service;
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
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//public class TeenPattiGameService {
//
//    private final List<String> deck = new ArrayList<>(); // Deck of cards
//    private int currentPot = 0;
//    private int currentBet = 0;
//    private static final int ANTE_AMOUNT = 100; // The fixed ante amount, can be adjusted
//
//    @Autowired
//    private PlayerRepository playerRepository;
//    @Autowired
//    private GameRepository gameRepository;
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//    public TeenPattiGameService() {
//        initializeDeck();
//    }
//
//    private void initializeDeck() {
//        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
//        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
//        for (String suit : suits) {
//            for (String rank : ranks) {
//                deck.add(rank + " of " + suit);
//            }
//        }
//        Collections.shuffle(deck);
//    }
//
//    public void joinGame(Long userId, Long gameId) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
//
//        Game game = gameRepository.findById(gameId)
//                .orElseGet(() -> {
//                    Game newGame = new Game();
//                    newGame.setStatus(GameStatus.ACTIVE);
//                    newGame.setStartTime(LocalDateTime.now());
//                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
//                    newGame.setTotalPot(0L);
//                    return gameRepository.save(newGame);
//                });
//        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//            throw new IllegalStateException("User has already joined this game.");
//        }
//
//        if (game.getCurrentPlayerCount() >= game.getMaxPlayers()) {
//            throw new IllegalStateException("Game is full. Cannot join.");
//        }
//        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
//        gameRepository.save(game);
//
//        PlayerGame playerGame = new PlayerGame();
//        playerGame.setUserId(userId);
//        playerGame.setGameId(game.getGameId());
//        playerGame.setUser(user);
//        playerGame.setGame(game);
//        playerGame.setBetAmount(0L);
//        playerGame.setResult(GameResult.PENDING);
//        playerGameRepository.save(playerGame);
//
//        if (game.getCurrentPlayerCount() >= game.getMinPlayers()) {
//            startGame(game);
//        }
//    }
//
//    private void startGame(Game game) {
//        game.setStatus(GameStatus.ACTIVE);
//        gameRepository.save(game);
//        broadcastEvent("gameStarted", "The game has started! Good luck to all players!");
//        postAnte(game.getGameId());
//        dealCards(game.getGameId());
//    }
//
//
//    public void postAnte(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        // Retrieve all players associated with this game
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            Long userId = playerGame.getUserId();
//
//            // Fetch the player's entity
//            Player player = playerRepository.findById(userId)
//                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//            // Check if the player has enough chips for the ante
//            if (player.getChips() < ANTE_AMOUNT) {
//                throw new IllegalStateException("Player " + player.getUser() + " does not have enough chips for the ante.");
//            }
//
//            // Deduct the ante from the player's chips
//            player.setChips(player.getChips() - ANTE_AMOUNT);
//            playerRepository.save(player); // Save the updated player state
//
//            // Add the ante amount to the player's bet amount
//            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
//            playerGameRepository.save(playerGame); // Save updated player-game state
//
//            // Add the ante to the game's current pot
//            currentPot += ANTE_AMOUNT;
//        }
//
//        System.out.println("Ante of " + ANTE_AMOUNT + " has been posted by all players.");
//        game.setTotalPot((long) currentPot); // Update the total pot in the game
//        gameRepository.save(game); // Save updated game state
//    }
//
//
//    public void dealCards(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//        if (game.getStatus() != GameStatus.ACTIVE) {
//            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
//        }
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        if (playersInGame.isEmpty()) {
//            throw new IllegalStateException("No players in the game to deal cards.");
//        }
//
//        for (PlayerGame playerGame : playersInGame) {
//            List<String> hand = new ArrayList<>();
//            for (int i = 0; i < 3; i++) {
//                if (!deck.isEmpty()) {
//                    hand.add(deck.remove(0)); // Draw cards from the top of the deck
//                } else {
//                    throw new IllegalStateException("Not enough cards in the deck.");
//                }
//            }
//            playerGame.setCards(String.join(",", hand));
//            playerGameRepository.save(playerGame);
//        }
//        System.out.println("Cards have been dealt to players in game ID: " + gameId);
//    }
//
//    public void foldPlayer(Long playerId, Long gameId) {
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//        playerGame.setHasFolded(true);
//        playerGame.setResult(GameResult.LOST);
//        playerGameRepository.save(playerGame);
//    }
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
//        if (activePlayers.isEmpty()) {
//            updatePlayerGameResults(playersInGame, Collections.emptyList()); // Handle folds
//            return visibleCards;
//        }
//
//        GameResult gameResult = determineWinner(activePlayers); // Pass only active players
//        updatePlayerGameResults(playersInGame, activePlayers); // Update results for all players
//        return visibleCards;
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
//
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
//    public void placeBet(Long playerId, int amount, Long gameId) {
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//
//        // Retrieve the player to access the chip count
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
//        playerRepository.save(player); // Save the updated player chip count
//
//
//
//        currentBet = Math.max(currentBet, amount);
//        currentPot += amount;
//
//        playerGame.setBetAmount(playerGame.getBetAmount() + amount);
//        playerGameRepository.save(playerGame);
//
//        if (isBettingRoundOver(gameId)) {
//            finishRound(gameId);
//        }
//    }
//    private boolean isBettingRoundOver(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        boolean allPlayersBetOrFolded = playersInGame.stream().allMatch(playerGame -> {
//            return playerGame.getHasFolded() || playerGame.getBetAmount() >= currentBet;
//        });
//        return allPlayersBetOrFolded;
//    }
//    public void finishRound(Long gameId) {
//        String winnerId = evaluateHands(gameId);
//        distributePot(Long.parseLong(winnerId), gameId);
//        endGame(gameId);
//    }
//
//    public void distributePot(Long winnerId, Long gameId) {
//        PlayerGame winnerGame = playerGameRepository.findByUserIdAndGameId(winnerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Winner not found"));
//        Player winner = playerRepository.findById(winnerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
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
//    private String evaluateHands(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        Map<String, String> playerResults = new HashMap<>();
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (!playerGame.getHasFolded()) {
//                List<Card> hand = convertStringToHand(Arrays.asList(playerGame.getCards().split(",")));
//                String handType = HandEvaluator.evaluateHand(hand); // Assuming HandEvaluator exists
//                playerResults.put(playerGame.getUserId().toString(), handType);
//            }
//        }
//        return determineBestHand(playerResults);
//    }
//
//    private String determineBestHand(Map<String, String> playerResults) {
//        List<String> handHierarchy = Arrays.asList("HIGH CARD", "PAIR", "COLOR", "SEQUENCE", "PURE SEQUENCE", "TRAIL");
//        String bestPlayer = null;
//        String bestHandType = null;
//
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
//
//
//
//
//    public void resetGame(Long gameId) {
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            playerGame.setCards(null);
//            playerGame.setBetAmount(0L);
//            playerGame.setHasFolded(false);
//            playerGame.setResult(GameResult.PENDING);
//            playerGameRepository.save(playerGame);
//        }
//        currentPot = 0;
//        currentBet = 0;
//        initializeDeck();
//    }
//
//    private void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}
