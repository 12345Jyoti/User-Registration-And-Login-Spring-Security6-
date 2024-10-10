package com.auth.AuthImpl.ctp.actionImpl;

import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.enums.GameCurrentStatus;
import com.auth.AuthImpl.ctp.enums.GameStatus;
import com.auth.AuthImpl.ctp.nenity.GameInstance;
import com.auth.AuthImpl.ctp.nenity.GamePlayer;
import com.auth.AuthImpl.ctp.nenity.GameTemplate;
import com.auth.AuthImpl.ctp.nenity.Player;
import com.auth.AuthImpl.registraion.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util .*;
import java.util.concurrent.*;
import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
import com.auth.AuthImpl.ctp.repository.GamePlayerRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class JoinGameAction {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameInstanceRepository gameInstanceRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private TurnManager turnManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private List<String> deck = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private GameInstance currentGameInstance;
    private static final int ANTE_AMOUNT = 100;
    private BigDecimal currentPot;
    private BigDecimal currentBet;

    @Transactional
    public void joinGame(Long playerId, Long gameId) {
        GameInstance gameInstance = gameInstanceRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Fetch the GameTemplate associated with this GameInstance
        GameTemplate gameTemplate = gameInstance.getGameTemplate();

        // Check if the game has reached the max number of players from GameTemplate
        if (gameInstance.getTotalJoinedPlayers() >= gameTemplate.getMaxPlayers()) {
            throw new RuntimeException("Game is full");
        }

        Optional<GamePlayer> existingPlayer = gamePlayerRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (existingPlayer.isPresent()) {
            throw new RuntimeException("Player has already joined this game.");
        }

        // Create a new GamePlayer object
        GamePlayer gamePlayer = new GamePlayer();

        // Set non-null fields
        gamePlayer.setPlayerId(playerId);
        gamePlayer.setGameId(gameId);
        gamePlayer.setGameCurrentStatus(GameCurrentStatus.blind);
        gamePlayer.setJoinedAt(new Date());
        gamePlayer.setPlayerBettingAmount(BigDecimal.ZERO);
        gamePlayer.setGameStatus(GameStatus.active);
        gamePlayer.setStatus(Status.LIVE);

        // Set the "created_by" field (cannot be null)
        gamePlayer.setCreatedBy("systemUser");

        // Save the new GamePlayer entry
        gamePlayerRepository.save(gamePlayer);

        // Increment total number of joined players
        gameInstance.setTotalJoinedPlayers(gameInstance.getTotalJoinedPlayers() + 1);
        gameInstanceRepository.save(gameInstance);

        updatePlayerOrder(gameInstance, playerId);

        // Broadcast an event to notify that the player has joined the game
        broadcastEvent("PLAYER_JOINED", "Player " + playerId + " has joined the game.");

        // Check if the game should start (e.g., if more than 3 players have joined)
        if (gameInstance.getTotalJoinedPlayers() >= 3) {
            checkAndStartGame(gameInstance);
// Start the game after a small delay
        }
    }

    private void updatePlayerOrder(GameInstance gameInstance, Long playerId) {
        Long gameId = gameInstance.getId();
        List<Long> playerOrder = turnManager.getPlayerOrder(gameId);

        if (playerOrder == null) {
            playerOrder = new ArrayList<>();
        }

        // Add the new player to the order
        playerOrder.add(playerId);

        // Update the player order in TurnManager
        turnManager.setPlayerOrder(gameId, playerOrder);

        // Set the first player to be the current player if it's the first joiner
        if (playerOrder.size() == 1) {
            turnManager.setCurrentPlayerId(gameId, playerId);
        }
    }

    private void checkAndStartGame(GameInstance gameInstance) {
        List<GamePlayer> players = gamePlayerRepository.findByGameId(gameInstance.getId());

        // Ensure we have at least 3 players to start the game
        if (players.size() >= 3) {
            // Sort players by joinedAt time to find the first player who joined
            players.sort(Comparator.comparing(GamePlayer::getJoinedAt));

            // Find the timestamp of when the first player joined
            Date firstPlayerJoinedAt = players.get(0).getJoinedAt();  // 0-based index (1st player)

            // Calculate the time difference between now and the first player's joinedAt time
            long timeSinceFirstPlayerJoined = new Date().getTime() - firstPlayerJoinedAt.getTime();
            long oneMinuteInMillis = TimeUnit.MINUTES.toMillis(1);

            System.out.println("Time since first player joined: " + timeSinceFirstPlayerJoined + " ms");

            if (timeSinceFirstPlayerJoined >= oneMinuteInMillis) {
                // If one minute has passed since the first player joined, start the game immediately
                System.out.println("Starting game now!");
                startGame(gameInstance);
            } else {
                // Otherwise, schedule the game to start after the remaining time
                long delay = oneMinuteInMillis - timeSinceFirstPlayerJoined;
                System.out.println("Scheduling game to start in: " + delay + " ms");

                scheduler.schedule(() -> {
                    System.out.println("Scheduled task executing: Starting game");
                    startGame(gameInstance);
                }, delay, TimeUnit.MILLISECONDS);
            }
        } else {
            // Log message if there are not enough players
            System.out.println("Not enough players to start the game. Current player count: " + players.size());
        }
    }


    private void startGame(GameInstance gameInstance) {
        currentGameInstance = gameInstance;  // Store the current game instance
        initializeDeck();  // Initialize the deck
        dealCards(gameInstance.getId(), 3); // Deal 3 cards to each player (adjust as needed)
        postAnte(gameInstance.getId());

        // Initialize player indices
        List<GamePlayer> players = gamePlayerRepository.findByGameId(gameInstance.getId());

        if (!players.isEmpty()) {
            // Set current player index to the first player (index 0)
            gameInstance.setCurrentPlayerIndex(0);

            // Set previous player index to null (no previous player at the start)
            gameInstance.setPrevPlayerIndex(null);

            // Set next player index to the second player (index 1) or null if only one player
            gameInstance.setNextPlayerIndex(players.size() > 1 ? 1 : null);

            // Save the updated game instance with the initialized indices
            gameInstanceRepository.save(gameInstance);
        }

        broadcastEvent("GAME_STARTED", "The game has started with more than 3 players!");
    }

    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        deck.clear();  // Clear the deck before initializin
        // Add each combination of rank and suit to the deck
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + " of " + suit);
            }
        }

        // Shuffle the deck
        Collections.shuffle(deck);

        // Broadcast that the deck has been initialized and shuffled
        broadcastEvent("DECK_INITIALIZED", "Deck initialized and shuffled.");
    }

// New method to deal cards to players

    public void dealCards(Long gameId, int cardsPerPlayer) {
        GameInstance gameInstance = gameInstanceRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (deck.isEmpty()) {
            initializeDeck();  // Initialize the deck if it's empty
        }

        List<GamePlayer> players = gamePlayerRepository.findByGameId(gameId);
        ObjectMapper objectMapper = new ObjectMapper();  // Jackson object mapper for JSON conversion

        for (GamePlayer player : players) {
            List<String> dealtCards = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                if (!deck.isEmpty()) {
                    dealtCards.add(deck.remove(0));  // Remove card from deck and add to player's hand
                }
            }

            try {
                String jsonCards = objectMapper.writeValueAsString(dealtCards);  // Convert List to JSON
                player.setCards(jsonCards);  // Set valid JSON in 'cards' field
            } catch (Exception e) {
                throw new RuntimeException("Error converting cards to JSON", e);  // Handle JSON conversion errors
            }

            gamePlayerRepository.save(player);  // Save the player with the updated cards

            // Broadcast cards dealt to the player
            broadcastEvent("CARDS_DEALT", "Player " + player.getPlayerId() + " has been dealt cards." );
        }
    }


    private void postAnte(Long gameId) {
        // Fetch the list of players
        List<GamePlayer> players = gamePlayerRepository.findByGameId(gameId);

        for (GamePlayer gamePlayer : players) {
            // Fetch the corresponding Player entity
            Player player = playerRepository.findById(gamePlayer.getPlayerId())
                    .orElseThrow(() -> new RuntimeException("Player not found"));

            // Check if the player has enough amount to post the ante
            if (player.getTotalAvailableAmount().compareTo(BigDecimal.valueOf(ANTE_AMOUNT)) >= 0) {
                // Update player's total available amount and betting amount
                player.setTotalAvailableAmount(player.getTotalAvailableAmount().subtract(BigDecimal.valueOf(ANTE_AMOUNT)));
                gamePlayer.setPlayerBettingAmount(gamePlayer.getPlayerBettingAmount().add(BigDecimal.valueOf(ANTE_AMOUNT)));

                // Save the updated player and gamePlayer
                playerRepository.save(player);
                gamePlayerRepository.save(gamePlayer);

                broadcastEvent("ANTE_POSTED", "Player " + player.getId() + " has posted an ante of " + ANTE_AMOUNT);
            } else {
                broadcastEvent("ANTE_FAILED", "Player " + player.getId() + " does not have enough funds to post the ante.");
            }
        }
    }
    protected void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }
}

//////
//////    private static final int ANTE_AMOUNT = 100; // Ante required
//////    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
//////    private static final int MAX_PLAYERS = 5;    // Maximum players
//////    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
//////    private final List<String> deck = new ArrayList<>(); // Deck of cards
//////    private final Object lock = new Object(); // Lock for synchronizing game start
//////
//////    // Initialize the deck when the object is created
//////    public JoinGameAction() {
//////        initializeDeck();
//////    }
//////
//////    // Method to initialize the deck of cards
//////    private void initializeDeck() {
//////        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
//////        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
//////        for (String suit : suits) {
//////            for (String rank : ranks) {
//////                deck.add(rank + " of " + suit);
//////            }
//////        }
//////        Collections.shuffle(deck);
//////    }
//////
//////    // Method for player joining the game
//////    public void joinGame(Long userId, Long gameId) {
//////        Player player = playerRepository.findById(userId)
//////                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//////
//////        Game game = gameRepository.findById(gameId)
//////                .orElseGet(() -> {
//////                    Game newGame = new Game();
//////                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
//////                    newGame.setStartTime(LocalDateTime.now());
//////                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
//////                    newGame.setTotalPot(0L);
//////                    return gameRepository.save(newGame);
//////                });
//////
//////        if (game.getStatus() == GameStatus.ACTIVE) {
//////            throw new IllegalStateException("Cannot join. The game has already started.");
//////        }
//////
//////        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//////            throw new IllegalStateException("Player has already joined this game.");
//////        }
//////
//////        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
//////            throw new IllegalStateException("Game is full. Cannot join.");
//////        }
//////
//////        if (player.getChips() < ANTE_AMOUNT) {
//////            broadcastEvent("Error", "Player does not have enough chips to join the game.");
//////            throw new IllegalStateException("Player does not have enough chips to join the game.");
//////        }
//////
//////        // Add player to the game
//////        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
//////        gameRepository.save(game);
//////
//////        PlayerGame playerGame = new PlayerGame();
//////        playerGame.setUserId(userId);
//////        playerGame.setGameId(gameId);
//////        playerGame.setBetAmount(0L);
//////        playerGameRepository.save(playerGame);
//////
//////        // Set the first player join time if not already set
//////        if (game.getCurrentPlayerCount() == 1 && game.getStartTime() == null) {
//////            game.setStartTime(LocalDateTime.now());
//////            gameRepository.save(game);
//////        }
//////
//////        // Start the game after 1 minute if the game is ready
//////        startGameWithDelay(game);
//////    }
//////
//////    private void startGameWithDelay(Game game) {
//////        CompletableFuture.runAsync(() -> {
//////            try {
//////                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
//////
//////                synchronized (lock) {
//////                    // Ensure game is still valid to start after 1 minute
//////                    Game currentGame = gameRepository.findById(game.getGameId())
//////                            .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//////
//////                    if (currentGame.getCurrentPlayerCount() >= MIN_PLAYERS && currentGame.getStatus() != GameStatus.ACTIVE) {
//////                        startGame(currentGame);
//////                    } else if (currentGame.getCurrentPlayerCount() < MIN_PLAYERS) {
//////                        broadcastEvent("Error", "Not enough players to start the game.");
//////                    }
//////                }
//////            } catch (InterruptedException e) {
//////                Thread.currentThread().interrupt();
//////                broadcastEvent("Error", "Game start interrupted.");
//////            }
//////        });
//////    }
//////
//////    private void startGame(Game game) {
//////        synchronized (lock) {
//////            // Check if the game is still inactive before starting it
//////            if (game.getStatus() == GameStatus.ACTIVE) {
//////                return; // Prevent starting the game again if it is already active
//////            }
//////
//////            game.setStatus(GameStatus.ACTIVE);
//////            game.setStartTime(LocalDateTime.now());
//////            gameRepository.save(game);
//////
//////            // Initialize the deck here to ensure it's ready for dealing cards
//////            initializeDeck();
//////
//////            long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
//////            dealCards(game.getGameId());
//////
//////            // Broadcast that the game has started and show the total ante
//////            broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
//////        }
//////    }
//////
//////    // Method to post ante
//////    private long postAnte(Long gameId) {
//////        long totalAnte = 0; // Initialize total ante
//////        Game game = gameRepository.findById(gameId)
//////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//////
//////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//////
//////        if (playersInGame.size() < MIN_PLAYERS) {
//////            throw new IllegalStateException("Not enough players to post ante.");
//////        }
//////
//////        for (PlayerGame playerGame : playersInGame) {
//////            Long userId = playerGame.getUserId();
//////            Player player = playerRepository.findById(userId)
//////                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//////
//////            if (player.getChips() < ANTE_AMOUNT) {
//////                throw new IllegalStateException("Player " + player.getPlayerId() + " does not have enough chips for the ante.");
//////            }
//////
//////            // Deduct the ante and update player info
//////            player.setChips(player.getChips() - ANTE_AMOUNT);
//////            playerRepository.save(player);
//////
//////            // Update player's bet and game's total pot
//////            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
//////            playerGameRepository.save(playerGame);
//////            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
//////            totalAnte += ANTE_AMOUNT;
//////        }
//////
//////        gameRepository.save(game);
//////        return totalAnte;
//////    }
//////
//////    // Method to deal cards
//////    public void dealCards(Long gameId) {
//////        Game game = gameRepository.findById(gameId)
//////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//////
//////        if (game.getStatus() != GameStatus.ACTIVE) {
//////            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
//////        }
//////
//////        // Initialize the deck if it's empty (as a safety check)
//////        if (deck.isEmpty()) {
//////            initializeDeck();  // Reinitialize the deck if it has been emptied
//////        }
//////
//////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//////
//////        for (PlayerGame playerGame : playersInGame) {
//////            // Initialize an empty hand if the player does not have any cards
//////            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
//////                List<String> hand = new ArrayList<>();
//////
//////                // Assign 3 cards to the player
//////                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
//////                    if (!deck.isEmpty()) {
//////                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
//////                    } else {
//////                        throw new IllegalStateException("Not enough cards in the deck.");
//////                    }
//////                }
//////
//////                // Set the player's hand by joining the cards with commas
//////                playerGame.setCards(String.join(",", hand));
//////                playerGameRepository.save(playerGame);
//////            }
//////        }
//////
//////        System.out.println("Cards have been dealt to players in game ID: " + gameId);
//////    }
//////
//////    // Method to broadcast events
//////    private void broadcastEvent(String eventType, Object data) {
//////        GameEvent event = new GameEvent();
//////        event.setEventType(eventType);
//////        event.setEventData(data);
//////        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//////    }
////}
//
//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.nenity.GameInstance;
//import com.auth.AuthImpl.ctp.nenity.GameTemplate;
//import com.auth.AuthImpl.ctp.nenity.Player;
//import com.auth.AuthImpl.ctp.nentity.Player;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.registraion.enums.Status;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
////@Service
////public class JoinGameAction {
////
////    @Autowired
////    private PlayerRepository playerRepository;
////
////    @Autowired
////    private GameRepository gameRepository;
////
////    @Autowired
////    private PlayerGameRepository playerGameRepository;
////
////    @Autowired
////    private SimpMessagingTemplate messagingTemplate;
////
////    @Autowired
////    private TurnManager turnManager; // Inject TurnManager
////
////    private static final int ANTE_AMOUNT = 100; // Ante required
////    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
////    private static final int MAX_PLAYERS = 5;    // Maximum players
////    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
////    private final List<String> deck = new ArrayList<>(); // Deck of cards
////    private final Object lock = new Object(); // Lock for synchronizing game start
////
////    // Initialize the deck when the object is created
////    public JoinGameAction() {
////        initializeDeck();
////    }
////
////    // Method to initialize the deck of cards
////    private void initializeDeck() {
////        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
////        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
////        for (String suit : suits) {
////            for (String rank : ranks) {
////                deck.add(rank + " of " + suit);
////            }
////        }
////        Collections.shuffle(deck);
////    }
////
////    // Method for player joining the game
////    public void joinGame(Long userId, Long gameId) {
////        Player player = playerRepository.findById(userId)
////                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
////
////        Game game = gameRepository.findById(gameId)
////                .orElseGet(() -> {
////                    Game newGame = new Game();
////                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
////                    newGame.setStartTime(LocalDateTime.now());
////                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
////                    newGame.setTotalPot(0L);
////                    return gameRepository.save(newGame);
////                });
////
////        if (game.getStatus() == GameStatus.ACTIVE) {
////            throw new IllegalStateException("Cannot join. The game has already started.");
////        }
////
////        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
////            throw new IllegalStateException("Player has already joined this game.");
////        }
////
////        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
////            throw new IllegalStateException("Game is full. Cannot join.");
////        }
////
////        if (player.getChips() < ANTE_AMOUNT) {
////            broadcastEvent("Error", "Player does not have enough chips to join the game.");
////            throw new IllegalStateException("Player does not have enough chips to join the game.");
////        }
////
////        // Add player to the game
////        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
////        gameRepository.save(game);
////
////        PlayerGame playerGame = new PlayerGame();
////        playerGame.setUserId(userId);
////        playerGame.setGameId(gameId);
////        playerGame.setBetAmount(0L);
////        playerGameRepository.save(playerGame);
////
////        // Set the first player join time if not already set
////        if (game.getCurrentPlayerCount() == 1 && game.getStartTime() == null) {
////            game.setStartTime(LocalDateTime.now());
////            gameRepository.save(game);
////        }
////
////        // Set the player order and current player after joining
////        List<PlayerGame> playerGames = playerGameRepository.findByGameId(gameId);
////        List<Long> playerIds = new ArrayList<>();
////        for (PlayerGame pg : playerGames) {
////            playerIds.add(pg.getUserId());
////        }
////        turnManager.setPlayerOrder(gameId, playerIds);
////        if (game.getCurrentPlayerCount() == 1) {
////            turnManager.setCurrentPlayerId(gameId, playerIds.get(0)); // Set the first player as current
////        }
////
////        // Start the game after 1 minute if the game is ready
////        startGameWithDelay(game);
////    }
////
////    private void startGameWithDelay(Game game) {
////        CompletableFuture.runAsync(() -> {
////            try {
////                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
////
////                synchronized (lock) {
////                    // Ensure game is still valid to start after 1 minute
////                    Game currentGame = gameRepository.findById(game.getGameId())
////                            .orElseThrow(() -> new IllegalArgumentException("Game not found"));
////
////                    if (currentGame.getCurrentPlayerCount() >= MIN_PLAYERS && currentGame.getStatus() != GameStatus.ACTIVE) {
////                        startGame(currentGame);
////                    } else if (currentGame.getCurrentPlayerCount() < MIN_PLAYERS) {
////                        broadcastEvent("Error", "Not enough players to start the game.");
////                    }
////                }
////            } catch (InterruptedException e) {
////                Thread.currentThread().interrupt();
////                broadcastEvent("Error", "Game start interrupted.");
////            }
////        });
////    }
////
////    private void startGame(Game game) {
////        synchronized (lock) {
////            // Check if the game is still inactive before starting it
////            if (game.getStatus() == GameStatus.ACTIVE) {
////                return; // Prevent starting the game again if it is already active
////            }
////
////            game.setStatus(GameStatus.ACTIVE);
////            game.setStartTime(LocalDateTime.now());
////            gameRepository.save(game);
////
////            // Initialize the deck here to ensure it's ready for dealing cards
////            initializeDeck();
////
////            long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
////            dealCards(game.getGameId());
////
////            // Broadcast that the game has started and show the total ante
////            broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
////        }
////    }
////
////    // Method to post ante
////    private long postAnte(Long gameId) {
////        long totalAnte = 0; // Initialize total ante
////        Game game = gameRepository.findById(gameId)
////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
////
////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
////
////        if (playersInGame.size() < MIN_PLAYERS) {
////            throw new IllegalStateException("Not enough players to post ante.");
////        }
////
////        for (PlayerGame playerGame : playersInGame) {
////            Long userId = playerGame.getUserId();
////            Player player = playerRepository.findById(userId)
////                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
////
////            if (player.getChips() < ANTE_AMOUNT) {
////                throw new IllegalStateException("Player " + player.getPlayerId() + " does not have enough chips for the ante.");
////            }
////
////            // Deduct the ante and update player info
////            player.setChips(player.getChips() - ANTE_AMOUNT);
////            playerRepository.save(player);
////
////            // Update player's bet and game's total pot
////            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
////            playerGameRepository.save(playerGame);
////            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
////            totalAnte += ANTE_AMOUNT;
////        }
////
////        gameRepository.save(game);
////        return totalAnte;
////    }
////
//////    / Method to deal cards
////    public void dealCards(Long gameId) {
////        Game game = gameRepository.findById(gameId)
////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
////
////        if (game.getStatus() != GameStatus.ACTIVE) {
////            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
////        }
////
////        // Initialize the deck if it's empty (as a safety check)
////        if (deck.isEmpty()) {
////            initializeDeck();  // Reinitialize the deck if it has been emptied
////        }
////
////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
////
////        for (PlayerGame playerGame : playersInGame) {
////            // Initialize an empty hand if the player does not have any cards
////            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
////                List<String> hand = new ArrayList<>();
////
////                // Assign 3 cards to the player
////                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
////                    if (!deck.isEmpty()) {
////                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
////                    } else {
////                        throw new IllegalStateException("Not enough cards in the deck.");
////                    }
////                }
////
////                // Set the player's hand by joining the cards with commas
////                playerGame.setCards(String.join(",", hand));
////                playerGameRepository.save(playerGame);
////            }
////        }
////
////        System.out.println("Cards have been dealt to players in game ID: " + gameId);
////    }
////
////    // Method to broadcast events
////    private void broadcastEvent(String eventType, Object data) {
////        GameEvent event = new GameEvent();
////        event.setEventType(eventType);
////        event.setEventData(data);
////        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
////    }
////}
//////package com.auth.AuthImpl.ctp.actionImpl;
//////
//////import com.auth.AuthImpl.ctp.dto.GameEvent;
//////import com.auth.AuthImpl.ctp.entity.Game;
//////import com.auth.AuthImpl.ctp.entity.Player;
//////import com.auth.AuthImpl.ctp.entity.PlayerGame;
//////import com.auth.AuthImpl.ctp.enums.GameStatus;
//////import com.auth.AuthImpl.ctp.repository.GameRepository;
//////import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//////import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//////import org.springframework.beans.factory.annotation.Autowired;
//////import org.springframework.messaging.simp.SimpMessagingTemplate;
//////import org.springframework.stereotype.Service;
//////
//////import java.time.LocalDateTime;
//////import java.util.ArrayList;
//////import java.util.Collections;
//////import java.util.List;
//////import java.util.concurrent.CompletableFuture;
//////import java.util.concurrent.TimeUnit;
//////
//////@Service
//////public class JoinGameAction {
//////
//////    @Autowired
//////    private PlayerRepository playerRepository;
//////
//////    @Autowired
//////    private GameRepository gameRepository;
//////
//////    @Autowired
//////    private PlayerGameRepository playerGameRepository;
//////
//////    @Autowired
//////    private SimpMessagingTemplate messagingTemplate;
//////
//////    private static final int ANTE_AMOUNT = 100; // Ante required
//////    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
//////    private static final int MAX_PLAYERS = 5;   // Maximum players
//////    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
//////    private final List<String> deck = new ArrayList<>(); // Deck of cards
//////
//////    // Initialize the deck when the object is created
//////    public JoinGameAction() {
//////        initializeDeck();
//////    }
//////
//////    // Method to initialize the deck of cards
//////    private void initializeDeck() {
//////        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
//////        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
//////        for (String suit : suits) {
//////            for (String rank : ranks) {
//////                deck.add(rank + " of " + suit);
//////            }
//////        }
//////        Collections.shuffle(deck);
//////    }
//////
//////    // Method for player joining the game
//////    // Method for player joining the game
//////    public void joinGame(Long userId, Long gameId) {
//////        Player player = playerRepository.findById(userId)
//////                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//////
//////        Game game = gameRepository.findById(gameId)
//////                .orElseGet(() -> {
//////                    Game newGame = new Game();
//////                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
//////                    newGame.setStartTime(LocalDateTime.now());
//////                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
//////                    newGame.setTotalPot(0L);
//////                    return gameRepository.save(newGame);
//////                });
//////
//////        if (game.getStatus() == GameStatus.ACTIVE) {
//////            throw new IllegalStateException("Cannot join. The game has already started.");
//////        }
//////
//////        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//////            throw new IllegalStateException("Player has already joined this game.");
//////        }
//////
//////        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
//////            throw new IllegalStateException("Game is full. Cannot join.");
//////        }
//////
//////        if (player.getChips() < ANTE_AMOUNT) {
//////            broadcastEvent("Error", "Player does not have enough chips to join the game.");
//////            throw new IllegalStateException("Player does not have enough chips to join the game.");
//////        }
//////
//////        // Add player to the game
//////        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
//////        gameRepository.save(game);
//////
//////        PlayerGame playerGame = new PlayerGame();
//////        playerGame.setUserId(userId);
//////        playerGame.setGameId(gameId);
//////        playerGame.setBetAmount(0L);
//////        playerGameRepository.save(playerGame);
//////
//////        // Set the first player join time if not already set
//////        if (game.getCurrentPlayerCount() == 1 && game.getStartTime() == null) {
//////            game.setStartTime(LocalDateTime.now());
//////            gameRepository.save(game);
//////        }
//////
//////        // Start the game after 1 minute if the game is ready
//////        startGameWithDelay(game);
//////    }
//////
//////    private synchronized void startGameWithDelay(Game game) {
//////        CompletableFuture.runAsync(() -> {
//////            try {
//////                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
//////
//////                // Ensure game is still valid to start after 1 minute
//////                if (game.getCurrentPlayerCount() >= MIN_PLAYERS) {
//////                    startGame(game);
//////                } else {
//////                    broadcastEvent("Error", "Not enough players to start the game.");
//////                }
//////            } catch (InterruptedException e) {
//////                Thread.currentThread().interrupt();
//////                broadcastEvent("Error", "Game start interrupted.");
//////            }
//////        });
//////    }
//////
//////    private void startGame(Game game) {
//////        if (game.getStatus() == GameStatus.ACTIVE) {
//////            return; // Prevent starting the game again if it is already active
//////        }
//////
//////        game.setStatus(GameStatus.ACTIVE);
//////        game.setStartTime(LocalDateTime.now());
//////        gameRepository.save(game);
//////
//////        // Initialize the deck here to ensure it's ready for dealing cards
//////        initializeDeck();
//////
//////        long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
//////        dealCards(game.getGameId());
//////
//////        // Broadcast that the game has started and show the total ante
//////        broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
//////    }
//////
//////    // Method to post ante
//////    private long postAnte(Long gameId) {
//////        long totalAnte = 0; // Initialize total ante
//////        Game game = gameRepository.findById(gameId)
//////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//////
//////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//////
//////        if (playersInGame.size() < MIN_PLAYERS) {
//////            throw new IllegalStateException("Not enough players to post ante.");
//////        }
//////
//////        for (PlayerGame playerGame : playersInGame) {
//////            Long userId = playerGame.getUserId();
//////            Player player = playerRepository.findById(userId)
//////                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//////
//////            if (player.getChips() < ANTE_AMOUNT) {
//////                throw new IllegalStateException("Player " + player.getUser() + " does not have enough chips for the ante.");
//////            }
//////
//////            // Deduct the ante and update player info
//////            player.setChips(player.getChips() - ANTE_AMOUNT);
//////            playerRepository.save(player);
//////
//////            // Update player's bet and game's total pot
//////            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
//////            playerGameRepository.save(playerGame);
//////            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
//////            totalAnte += ANTE_AMOUNT;
//////        }
//////
//////        gameRepository.save(game);
//////        return totalAnte;
//////    }
//////
//////    // Method to deal cards
//////    public void dealCards(Long gameId) {
//////        Game game = gameRepository.findById(gameId)
//////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//////
//////        if (game.getStatus() != GameStatus.ACTIVE) {
//////            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
//////        }
//////
//////        // Initialize the deck if it's empty (as a safety check)
//////        if (deck.isEmpty()) {
//////            initializeDeck();  // Reinitialize the deck if it has been emptied
//////        }
//////
//////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//////
//////        for (PlayerGame playerGame : playersInGame) {
//////            // Initialize an empty hand if the player does not have any cards
//////            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
//////                List<String> hand = new ArrayList<>();
//////
//////                // Assign 3 cards to the player
//////                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
//////                    if (!deck.isEmpty()) {
//////                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
//////                    } else {
//////                        throw new IllegalStateException("Not enough cards in the deck.");
//////                    }
//////                }
//////
//////                // Set the player's hand by joining the cards with commas
//////                playerGame.setCards(String.join(",", hand));
//////                playerGameRepository.save(playerGame);
//////            }
//////        }
//////
//////        System.out.println("Cards have been dealt to players in game ID: " + gameId);
//////    }
//////
//////    // Method to broadcast events
//////    private void broadcastEvent(String eventType, Object data) {
//////        GameEvent event = new GameEvent();
//////        event.setEventType(eventType);
//////        event.setEventData(data);
//////        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//////    }
//////}
////
////////
////////import com.auth.AuthImpl.ctp.dto.GameEvent;
////////import com.auth.AuthImpl.ctp.entity.Game;
////////import com.auth.AuthImpl.ctp.entity.Player;
////////import com.auth.AuthImpl.ctp.entity.PlayerGame;
////////import com.auth.AuthImpl.ctp.enums.GameStatus;
////////import com.auth.AuthImpl.ctp.repository.GameRepository;
////////import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
////////import com.auth.AuthImpl.ctp.repository.PlayerRepository;
////////import org.springframework.beans.factory.annotation.Autowired;
////////import org.springframework.messaging.simp.SimpMessagingTemplate;
////////import org.springframework.stereotype.Service;
////////
////////import java.time.LocalDateTime;
////////import java.util.ArrayList;
////////import java.util.Collections;
////////import java.util.List;
////////import java.util.concurrent.CompletableFuture;
////////import java.util.concurrent.TimeUnit;
////////
////////@Service
////////public class JoinGameAction {
////////
////////    @Autowired
////////    private PlayerRepository playerRepository;
////////
////////    @Autowired
////////    private GameRepository gameRepository;
////////
////////    @Autowired
////////    private PlayerGameRepository playerGameRepository;
////////
////////    @Autowired
////////    private SimpMessagingTemplate messagingTemplate;
////////
////////    private static final int ANTE_AMOUNT = 100; // Ante required
////////    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
////////    private static final int MAX_PLAYERS = 5;   // Maximum players
////////    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
////////    private final List<String> deck = new ArrayList<>(); // Deck of cards
////////
////////    // Initialize the deck when the object is created
////////    public JoinGameAction() {
////////        initializeDeck();
////////    }
////////
////////    // Method to initialize the deck of cards
////////    private void initializeDeck() {
////////        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
////////        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
////////        for (String suit : suits) {
////////            for (String rank : ranks) {
////////                deck.add(rank + " of " + suit);
////////            }
////////        }
////////        Collections.shuffle(deck);
////////    }
////////
////////    // Method for player joining the game
////////    public void joinGame(Long userId, Long gameId) {
////////        Player player = playerRepository.findById(userId)
////////                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
////////
////////        Game game = gameRepository.findById(gameId)
////////                .orElseGet(() -> {
////////                    Game newGame = new Game();
////////                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
////////                    newGame.setStartTime(LocalDateTime.now());
////////                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
////////                    newGame.setTotalPot(0L);
////////                    return gameRepository.save(newGame);
////////                });
////////
////////        if (game.getStatus() == GameStatus.ACTIVE) {
////////            throw new IllegalStateException("Cannot join. The game has already started.");
////////        }
////////
////////        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
////////            throw new IllegalStateException("Player has already joined this game.");
////////        }
////////
////////        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
////////            throw new IllegalStateException("Game is full. Cannot join.");
////////        }
////////
////////        if (player.getChips() < ANTE_AMOUNT) {
////////            broadcastEvent("Error", "Player does not have enough chips to join the game.");
////////            throw new IllegalStateException("Player does not have enough chips to join the game.");
////////        }
////////
////////        // Add player to the game
////////        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
////////        gameRepository.save(game);
////////
////////        PlayerGame playerGame = new PlayerGame();
////////        playerGame.setUserId(userId);
////////        playerGame.setGameId(gameId);
////////        playerGame.setBetAmount(0L);
////////        playerGameRepository.save(playerGame);
////////
////////        // Set the first player join time if not already set
////////        if (game.getCurrentPlayerCount() == 1 && game.getStartTime() == null) {
////////            game.setStartTime(LocalDateTime.now());
////////            gameRepository.save(game);
////////        }
////////
////////        // Start game after 1 minute if the game is ready
////////        if (game.getCurrentPlayerCount() >= MIN_PLAYERS) {
////////            startGameWithDelay(game);
////////        }
////////    }
////////
////////    // Method to start the game with a delay of 1 minute
////////    private void startGameWithDelay(Game game) {
////////        CompletableFuture.runAsync(() -> {
////////            try {
////////                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
////////                if (game.getCurrentPlayerCount() >= MIN_PLAYERS) {
////////                    startGame(game);
////////                } else {
////////                    broadcastEvent("Error", "Not enough players to start the game.");
////////                }
////////            } catch (InterruptedException e) {
////////                Thread.currentThread().interrupt();
////////            }
////////        });
////////    }
////////
////////    // Method to start the game
////////    private void startGame(Game game) {
////////        game.setStatus(GameStatus.ACTIVE);
////////        game.setStartTime(LocalDateTime.now());
////////        gameRepository.save(game);
////////
////////        // Initialize the deck here to ensure it's ready for dealing cards
////////        initializeDeck();
////////
////////        long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
////////        dealCards(game.getGameId());
////////
////////        // Broadcast that the game has started and show the total ante
////////        broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
////////    }
////////
////////    // ... (Other methods remain unchanged)
////////
////////    // Method to post ante
////////    private long postAnte(Long gameId) {
////////        long totalAnte = 0; // Initialize total ante
////////        Game game = gameRepository.findById(gameId)
////////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
////////
////////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
////////
////////        if (playersInGame.size() < MIN_PLAYERS) {
////////            throw new IllegalStateException("Not enough players to post ante.");
////////        }
////////
////////        for (PlayerGame playerGame : playersInGame) {
////////            Long userId = playerGame.getUserId();
////////            Player player = playerRepository.findById(userId)
////////                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
////////
////////            if (player.getChips() < ANTE_AMOUNT) {
////////                throw new IllegalStateException("Player " + player.getUser() + " does not have enough chips for the ante.");
////////            }
////////
////////            // Deduct the ante and update player info
////////            player.setChips(player.getChips() - ANTE_AMOUNT);
////////            playerRepository.save(player);
////////
////////            // Update player's bet and game's total pot
////////            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
////////            playerGameRepository.save(playerGame);
////////            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
////////            totalAnte += ANTE_AMOUNT;
////////        }
////////
////////        gameRepository.save(game);
////////        return totalAnte;
////////    }
////////
////////    // Method to deal cards
////////    public void dealCards(Long gameId) {
////////        Game game = gameRepository.findById(gameId)
////////                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
////////
////////        if (game.getStatus() != GameStatus.ACTIVE) {
////////            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
////////        }
////////
////////        // Initialize the deck if it's empty (as a safety check)
////////        if (deck.isEmpty()) {
////////            initializeDeck();  // Reinitialize the deck if it has been emptied
////////        }
////////
////////        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
////////
////////        for (PlayerGame playerGame : playersInGame) {
////////            // Initialize an empty hand if the player does not have any cards
////////            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
////////                List<String> hand = new ArrayList<>();
////////
////////                // Assign 3 cards to the player
////////                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
////////                    if (!deck.isEmpty()) {
////////                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
////////                    } else {
////////                        throw new IllegalStateException("Not enough cards in the deck.");
////////                    }
////////                }
////////
////////                // Set the player's hand by joining the cards with commas
////////                playerGame.setCards(String.join(",", hand));
////////                playerGameRepository.save(playerGame);
////////            }
////////        }
////////
////////        System.out.println("Cards have been dealt to players in game ID: " + gameId);
////////    }
////////
////////    // Method to broadcast events
////////    private void broadcastEvent(String eventType, Object data) {
////////        GameEvent event = new GameEvent();
////////        event.setEventType(eventType);
////////        event.setEventData(data);
////////        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
////////    }
////////}
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class JoinGameAction {
//
//    private static final int MAX_PLAYERS = 4; // Example max players
//    private static final int MIN_PLAYERS = 3; // Minimum players to start
//    private static final long ANTE_AMOUNT = 100L; // Example ante amount
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private GameInstanceRepository gameRepository;
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private TurnManager turnManager;
//
//    private final Object lock = new Object(); // Lock for synchronization
//
//    // Method for player joining the game
//    public void joinGame(Long userId, Long gameId) {
//        Player player = playerRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//
//        GameInstance game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found")); // No creation of game
//
//        // Check if the game is active
//        if (game.getGameStatus() == GameInstance.GameStatus.active) {
//            throw new IllegalStateException("Cannot join. The game has already started.");
//        }
//
//        // Check if the player has already joined
//        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//            throw new IllegalStateException("Player has already joined this game.");
//        }
//
//        // Check if the game is full
////        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
////            throw new IllegalStateException("Game is full. Cannot join.");
////        }
//
//        // Check if the player has enough chips
//            if (player.getTotalAvailableAmount().compareTo(BigDecimal.valueOf(ANTE_AMOUNT)) < 0) {
//                broadcastEvent("Error", "Player does not have enough chips to join the game.");
//                throw new IllegalStateException("Player does not have enough chips to join the game.");
//            }
//
//        // Add player to the game
//        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
//        gameRepository.save(game);
//
//        PlayerGame playerGame = new PlayerGame();
//        playerGame.setUserId(userId);
//        playerGame.setGameId(gameId);
//        playerGame.setBetAmount(0L);
//        playerGameRepository.save(playerGame);
//
//        // Set the first player join time if not already set
//        if (game.getCurrentPlayerCount() == 1 && game.getStartTime() == null) {
//            game.setStartTime(LocalDateTime.now());
//            gameRepository.save(game);
//        }
//
//        // Set the player order and current player after joining
//        List<PlayerGame> playerGames = playerGameRepository.findByGameId(gameId);
//        List<Long> playerIds = new ArrayList<>();
//        for (PlayerGame pg : playerGames) {
//            playerIds.add(pg.getUserId());
//        }
//
//        turnManager.setPlayerOrder(gameId, playerIds);
//        if (game.getCurrentPlayerCount() == 1) {
//            turnManager.setCurrentPlayerId(gameId, playerIds.get(0)); // Set the first player as current
//        }
//
//        // Start the game after 1 minute or extend if necessary
//        startGameWithDelay(game);
//    }
//
//    private void startGameWithDelay(Game game) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                // Wait for 1 minute (60 seconds) to check if the game can start
//                TimeUnit.MINUTES.sleep(1);
//
//                synchronized (lock) {
//                    // Retrieve the current state of the game after the wait
//                    Game currentGame = gameRepository.findById(game.getGameId())
//                            .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//                    // Fetch the GameTemplate associated with the current game
//                    GameTemplate gameTemplate = currentGame.getGameTemplate();
//                    int minPlayers = gameTemplate.getMinPlayers(); // Get the minimum player count from GameTemplate
//
//                    // Check if there are enough players to start the game
//                    if (currentGame.getCurrentPlayerCount() >= minPlayers && currentGame.getStatus() != GameStatus.ACTIVE) {
//                        startGame(currentGame);
//                    } else if (currentGame.getCurrentPlayerCount() < minPlayers) {
//                        // Inform players that the game will be extended by 10 seconds
//                        broadcastEvent("Info", "Not enough players to start the game. Extending time by 10 seconds.");
//
//                        // Extend time for 10 seconds
//                        TimeUnit.SECONDS.sleep(10);
//
//                        // Re-check player count after the extension
//                        currentGame = gameRepository.findById(game.getGameId())
//                                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//                        // Attempt to start the game again after the extension
//                        if (currentGame.getCurrentPlayerCount() >= minPlayers && currentGame.getStatus() != GameStatus.ACTIVE) {
//                            startGame(currentGame);
//                        } else {
//                            broadcastEvent("Error", "Still not enough players to start the game.");
//                        }
//                    }
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt(); // Restore interrupted status
//                broadcastEvent("Error", "Game start interrupted.");
//            }
//        });
//    }
//
//    private void startGame(GameInstance game) {
//        synchronized (lock) {
//            if (game.getGameStatus() == GameInstance.GameStatus.active) {
//                return; // Prevent starting the game again if it is already active
//            }
//
//            game.setGameStatus(GameInstance.GameStatus.active);
//            game.setStartTime(LocalDateTime.now());
//            gameRepository.save(game);
//
//            // Initialize the deck here to ensure it's ready for dealing cards
//            initializeDeck();
//
//            long totalAnte = postAnte(game.getId()); // Get the total ante amount
//            dealCards(game.getId());
//
//            // Broadcast that the game has started and show the total ante
//            broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
//        }
//    }
//
//    // Placeholder for broadcasting events
//    private void broadcastEvent(String eventType, String message) {
//        // Implement event broadcasting logic here
//    }
//
//    // Placeholder for deck initialization
//    private void initializeDeck() {
//        // Implement deck initialization logic here
//    }
//
//    // Placeholder for posting ante amounts
//    private long postAnte(Long gameId) {
//        // Implement ante posting logic here and return total ante
//        return ANTE_AMOUNT * MAX_PLAYERS; // Example return
//    }
//
//    // Placeholder for dealing cards
//    private void dealCards(Long gameId) {
//        // Implement card dealing logic here
//    }
//}
//

