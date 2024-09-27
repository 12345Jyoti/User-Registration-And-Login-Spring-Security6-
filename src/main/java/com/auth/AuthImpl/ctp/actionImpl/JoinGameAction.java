//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.entity.Player;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class JoinGameAction {
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    private static final int ANTE_AMOUNT = 100; // Ante required
//    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
//    private static final int MAX_PLAYERS = 5;    // Maximum players
//    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
//    private final List<String> deck = new ArrayList<>(); // Deck of cards
//    private final Object lock = new Object(); // Lock for synchronizing game start
//
//    // Initialize the deck when the object is created
//    public JoinGameAction() {
//        initializeDeck();
//    }
//
//    // Method to initialize the deck of cards
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
//    // Method for player joining the game
//    public void joinGame(Long userId, Long gameId) {
//        Player player = playerRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//
//        Game game = gameRepository.findById(gameId)
//                .orElseGet(() -> {
//                    Game newGame = new Game();
//                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
//                    newGame.setStartTime(LocalDateTime.now());
//                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
//                    newGame.setTotalPot(0L);
//                    return gameRepository.save(newGame);
//                });
//
//        if (game.getStatus() == GameStatus.ACTIVE) {
//            throw new IllegalStateException("Cannot join. The game has already started.");
//        }
//
//        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//            throw new IllegalStateException("Player has already joined this game.");
//        }
//
//        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
//            throw new IllegalStateException("Game is full. Cannot join.");
//        }
//
//        if (player.getChips() < ANTE_AMOUNT) {
//            broadcastEvent("Error", "Player does not have enough chips to join the game.");
//            throw new IllegalStateException("Player does not have enough chips to join the game.");
//        }
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
//        // Start the game after 1 minute if the game is ready
//        startGameWithDelay(game);
//    }
//
//    private void startGameWithDelay(Game game) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
//
//                synchronized (lock) {
//                    // Ensure game is still valid to start after 1 minute
//                    Game currentGame = gameRepository.findById(game.getGameId())
//                            .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//                    if (currentGame.getCurrentPlayerCount() >= MIN_PLAYERS && currentGame.getStatus() != GameStatus.ACTIVE) {
//                        startGame(currentGame);
//                    } else if (currentGame.getCurrentPlayerCount() < MIN_PLAYERS) {
//                        broadcastEvent("Error", "Not enough players to start the game.");
//                    }
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                broadcastEvent("Error", "Game start interrupted.");
//            }
//        });
//    }
//
//    private void startGame(Game game) {
//        synchronized (lock) {
//            // Check if the game is still inactive before starting it
//            if (game.getStatus() == GameStatus.ACTIVE) {
//                return; // Prevent starting the game again if it is already active
//            }
//
//            game.setStatus(GameStatus.ACTIVE);
//            game.setStartTime(LocalDateTime.now());
//            gameRepository.save(game);
//
//            // Initialize the deck here to ensure it's ready for dealing cards
//            initializeDeck();
//
//            long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
//            dealCards(game.getGameId());
//
//            // Broadcast that the game has started and show the total ante
//            broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
//        }
//    }
//
//    // Method to post ante
//    private long postAnte(Long gameId) {
//        long totalAnte = 0; // Initialize total ante
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        if (playersInGame.size() < MIN_PLAYERS) {
//            throw new IllegalStateException("Not enough players to post ante.");
//        }
//
//        for (PlayerGame playerGame : playersInGame) {
//            Long userId = playerGame.getUserId();
//            Player player = playerRepository.findById(userId)
//                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//            if (player.getChips() < ANTE_AMOUNT) {
//                throw new IllegalStateException("Player " + player.getPlayerId() + " does not have enough chips for the ante.");
//            }
//
//            // Deduct the ante and update player info
//            player.setChips(player.getChips() - ANTE_AMOUNT);
//            playerRepository.save(player);
//
//            // Update player's bet and game's total pot
//            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
//            playerGameRepository.save(playerGame);
//            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
//            totalAnte += ANTE_AMOUNT;
//        }
//
//        gameRepository.save(game);
//        return totalAnte;
//    }
//
//    // Method to deal cards
//    public void dealCards(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        if (game.getStatus() != GameStatus.ACTIVE) {
//            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
//        }
//
//        // Initialize the deck if it's empty (as a safety check)
//        if (deck.isEmpty()) {
//            initializeDeck();  // Reinitialize the deck if it has been emptied
//        }
//
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            // Initialize an empty hand if the player does not have any cards
//            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
//                List<String> hand = new ArrayList<>();
//
//                // Assign 3 cards to the player
//                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
//                    if (!deck.isEmpty()) {
//                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
//                    } else {
//                        throw new IllegalStateException("Not enough cards in the deck.");
//                    }
//                }
//
//                // Set the player's hand by joining the cards with commas
//                playerGame.setCards(String.join(",", hand));
//                playerGameRepository.save(playerGame);
//            }
//        }
//
//        System.out.println("Cards have been dealt to players in game ID: " + gameId);
//    }
//
//    // Method to broadcast events
//    private void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}
package com.auth.AuthImpl.ctp.actionImpl;

import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.entity.Game;
import com.auth.AuthImpl.ctp.entity.Player;
import com.auth.AuthImpl.ctp.entity.PlayerGame;
import com.auth.AuthImpl.ctp.enums.GameStatus;
import com.auth.AuthImpl.ctp.repository.GameRepository;
import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import com.auth.AuthImpl.ctp.actionImpl.TurnManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class JoinGameAction {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerGameRepository playerGameRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TurnManager turnManager; // Inject TurnManager

    private static final int ANTE_AMOUNT = 100; // Ante required
    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
    private static final int MAX_PLAYERS = 5;    // Maximum players
    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
    private final List<String> deck = new ArrayList<>(); // Deck of cards
    private final Object lock = new Object(); // Lock for synchronizing game start

    // Initialize the deck when the object is created
    public JoinGameAction() {
        initializeDeck();
    }

    // Method to initialize the deck of cards
    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + " of " + suit);
            }
        }
        Collections.shuffle(deck);
    }

    // Method for player joining the game
    public void joinGame(Long userId, Long gameId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Game game = gameRepository.findById(gameId)
                .orElseGet(() -> {
                    Game newGame = new Game();
                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
                    newGame.setStartTime(LocalDateTime.now());
                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
                    newGame.setTotalPot(0L);
                    return gameRepository.save(newGame);
                });

        if (game.getStatus() == GameStatus.ACTIVE) {
            throw new IllegalStateException("Cannot join. The game has already started.");
        }

        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
            throw new IllegalStateException("Player has already joined this game.");
        }

        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
            throw new IllegalStateException("Game is full. Cannot join.");
        }

        if (player.getChips() < ANTE_AMOUNT) {
            broadcastEvent("Error", "Player does not have enough chips to join the game.");
            throw new IllegalStateException("Player does not have enough chips to join the game.");
        }

        // Add player to the game
        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
        gameRepository.save(game);

        PlayerGame playerGame = new PlayerGame();
        playerGame.setUserId(userId);
        playerGame.setGameId(gameId);
        playerGame.setBetAmount(0L);
        playerGameRepository.save(playerGame);

        // Set the first player join time if not already set
        if (game.getCurrentPlayerCount() == 1 && game.getStartTime() == null) {
            game.setStartTime(LocalDateTime.now());
            gameRepository.save(game);
        }

        // Set the player order and current player after joining
        List<PlayerGame> playerGames = playerGameRepository.findByGameId(gameId);
        List<Long> playerIds = new ArrayList<>();
        for (PlayerGame pg : playerGames) {
            playerIds.add(pg.getUserId());
        }
        turnManager.setPlayerOrder(gameId, playerIds);
        if (game.getCurrentPlayerCount() == 1) {
            turnManager.setCurrentPlayerId(gameId, playerIds.get(0)); // Set the first player as current
        }

        // Start the game after 1 minute if the game is ready
        startGameWithDelay(game);
    }

    private void startGameWithDelay(Game game) {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute

                synchronized (lock) {
                    // Ensure game is still valid to start after 1 minute
                    Game currentGame = gameRepository.findById(game.getGameId())
                            .orElseThrow(() -> new IllegalArgumentException("Game not found"));

                    if (currentGame.getCurrentPlayerCount() >= MIN_PLAYERS && currentGame.getStatus() != GameStatus.ACTIVE) {
                        startGame(currentGame);
                    } else if (currentGame.getCurrentPlayerCount() < MIN_PLAYERS) {
                        broadcastEvent("Error", "Not enough players to start the game.");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                broadcastEvent("Error", "Game start interrupted.");
            }
        });
    }

    private void startGame(Game game) {
        synchronized (lock) {
            // Check if the game is still inactive before starting it
            if (game.getStatus() == GameStatus.ACTIVE) {
                return; // Prevent starting the game again if it is already active
            }

            game.setStatus(GameStatus.ACTIVE);
            game.setStartTime(LocalDateTime.now());
            gameRepository.save(game);

            // Initialize the deck here to ensure it's ready for dealing cards
            initializeDeck();

            long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
            dealCards(game.getGameId());

            // Broadcast that the game has started and show the total ante
            broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
        }
    }

    // Method to post ante
    private long postAnte(Long gameId) {
        long totalAnte = 0; // Initialize total ante
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);

        if (playersInGame.size() < MIN_PLAYERS) {
            throw new IllegalStateException("Not enough players to post ante.");
        }

        for (PlayerGame playerGame : playersInGame) {
            Long userId = playerGame.getUserId();
            Player player = playerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));

            if (player.getChips() < ANTE_AMOUNT) {
                throw new IllegalStateException("Player " + player.getPlayerId() + " does not have enough chips for the ante.");
            }

            // Deduct the ante and update player info
            player.setChips(player.getChips() - ANTE_AMOUNT);
            playerRepository.save(player);

            // Update player's bet and game's total pot
            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
            playerGameRepository.save(playerGame);
            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
            totalAnte += ANTE_AMOUNT;
        }

        gameRepository.save(game);
        return totalAnte;
    }

//    / Method to deal cards
    public void dealCards(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (game.getStatus() != GameStatus.ACTIVE) {
            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
        }

        // Initialize the deck if it's empty (as a safety check)
        if (deck.isEmpty()) {
            initializeDeck();  // Reinitialize the deck if it has been emptied
        }

        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);

        for (PlayerGame playerGame : playersInGame) {
            // Initialize an empty hand if the player does not have any cards
            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
                List<String> hand = new ArrayList<>();

                // Assign 3 cards to the player
                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
                    if (!deck.isEmpty()) {
                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
                    } else {
                        throw new IllegalStateException("Not enough cards in the deck.");
                    }
                }

                // Set the player's hand by joining the cards with commas
                playerGame.setCards(String.join(",", hand));
                playerGameRepository.save(playerGame);
            }
        }

        System.out.println("Cards have been dealt to players in game ID: " + gameId);
    }

    // Method to broadcast events
    private void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }
}
//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.entity.Player;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class JoinGameAction {
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    private static final int ANTE_AMOUNT = 100; // Ante required
//    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
//    private static final int MAX_PLAYERS = 5;   // Maximum players
//    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
//    private final List<String> deck = new ArrayList<>(); // Deck of cards
//
//    // Initialize the deck when the object is created
//    public JoinGameAction() {
//        initializeDeck();
//    }
//
//    // Method to initialize the deck of cards
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
//    // Method for player joining the game
//    // Method for player joining the game
//    public void joinGame(Long userId, Long gameId) {
//        Player player = playerRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//
//        Game game = gameRepository.findById(gameId)
//                .orElseGet(() -> {
//                    Game newGame = new Game();
//                    newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);
//                    newGame.setStartTime(LocalDateTime.now());
//                    newGame.setEndTime(LocalDateTime.now().plusHours(1));
//                    newGame.setTotalPot(0L);
//                    return gameRepository.save(newGame);
//                });
//
//        if (game.getStatus() == GameStatus.ACTIVE) {
//            throw new IllegalStateException("Cannot join. The game has already started.");
//        }
//
//        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//            throw new IllegalStateException("Player has already joined this game.");
//        }
//
//        if (game.getCurrentPlayerCount() >= MAX_PLAYERS) {
//            throw new IllegalStateException("Game is full. Cannot join.");
//        }
//
//        if (player.getChips() < ANTE_AMOUNT) {
//            broadcastEvent("Error", "Player does not have enough chips to join the game.");
//            throw new IllegalStateException("Player does not have enough chips to join the game.");
//        }
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
//        // Start the game after 1 minute if the game is ready
//        startGameWithDelay(game);
//    }
//
//    private synchronized void startGameWithDelay(Game game) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
//
//                // Ensure game is still valid to start after 1 minute
//                if (game.getCurrentPlayerCount() >= MIN_PLAYERS) {
//                    startGame(game);
//                } else {
//                    broadcastEvent("Error", "Not enough players to start the game.");
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                broadcastEvent("Error", "Game start interrupted.");
//            }
//        });
//    }
//
//    private void startGame(Game game) {
//        if (game.getStatus() == GameStatus.ACTIVE) {
//            return; // Prevent starting the game again if it is already active
//        }
//
//        game.setStatus(GameStatus.ACTIVE);
//        game.setStartTime(LocalDateTime.now());
//        gameRepository.save(game);
//
//        // Initialize the deck here to ensure it's ready for dealing cards
//        initializeDeck();
//
//        long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
//        dealCards(game.getGameId());
//
//        // Broadcast that the game has started and show the total ante
//        broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
//    }
//
//    // Method to post ante
//    private long postAnte(Long gameId) {
//        long totalAnte = 0; // Initialize total ante
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        if (playersInGame.size() < MIN_PLAYERS) {
//            throw new IllegalStateException("Not enough players to post ante.");
//        }
//
//        for (PlayerGame playerGame : playersInGame) {
//            Long userId = playerGame.getUserId();
//            Player player = playerRepository.findById(userId)
//                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//            if (player.getChips() < ANTE_AMOUNT) {
//                throw new IllegalStateException("Player " + player.getUser() + " does not have enough chips for the ante.");
//            }
//
//            // Deduct the ante and update player info
//            player.setChips(player.getChips() - ANTE_AMOUNT);
//            playerRepository.save(player);
//
//            // Update player's bet and game's total pot
//            playerGame.setBetAmount(playerGame.getBetAmount() + ANTE_AMOUNT);
//            playerGameRepository.save(playerGame);
//            game.setTotalPot(game.getTotalPot() + ANTE_AMOUNT);
//            totalAnte += ANTE_AMOUNT;
//        }
//
//        gameRepository.save(game);
//        return totalAnte;
//    }
//
//    // Method to deal cards
//    public void dealCards(Long gameId) {
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        if (game.getStatus() != GameStatus.ACTIVE) {
//            throw new IllegalStateException("Cannot deal cards; the game is not in progress.");
//        }
//
//        // Initialize the deck if it's empty (as a safety check)
//        if (deck.isEmpty()) {
//            initializeDeck();  // Reinitialize the deck if it has been emptied
//        }
//
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            // Initialize an empty hand if the player does not have any cards
//            if (playerGame.getCards() == null || playerGame.getCards().isEmpty()) {
//                List<String> hand = new ArrayList<>();
//
//                // Assign 3 cards to the player
//                for (int i = 0; i < CARDS_PER_PLAYER; i++) {
//                    if (!deck.isEmpty()) {
//                        hand.add(deck.remove(0));  // Deal a card from the top of the deck
//                    } else {
//                        throw new IllegalStateException("Not enough cards in the deck.");
//                    }
//                }
//
//                // Set the player's hand by joining the cards with commas
//                playerGame.setCards(String.join(",", hand));
//                playerGameRepository.save(playerGame);
//            }
//        }
//
//        System.out.println("Cards have been dealt to players in game ID: " + gameId);
//    }
//
//    // Method to broadcast events
//    private void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}

////
////import com.auth.AuthImpl.ctp.dto.GameEvent;
////import com.auth.AuthImpl.ctp.entity.Game;
////import com.auth.AuthImpl.ctp.entity.Player;
////import com.auth.AuthImpl.ctp.entity.PlayerGame;
////import com.auth.AuthImpl.ctp.enums.GameStatus;
////import com.auth.AuthImpl.ctp.repository.GameRepository;
////import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
////import com.auth.AuthImpl.ctp.repository.PlayerRepository;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.messaging.simp.SimpMessagingTemplate;
////import org.springframework.stereotype.Service;
////
////import java.time.LocalDateTime;
////import java.util.ArrayList;
////import java.util.Collections;
////import java.util.List;
////import java.util.concurrent.CompletableFuture;
////import java.util.concurrent.TimeUnit;
////
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
////    private static final int ANTE_AMOUNT = 100; // Ante required
////    private static final int MIN_PLAYERS = 2;   // Minimum number of players to start
////    private static final int MAX_PLAYERS = 5;   // Maximum players
////    private static final int CARDS_PER_PLAYER = 3; // Number of cards dealt to each player
////    private final List<String> deck = new ArrayList<>(); // Deck of cards
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
////        // Start game after 1 minute if the game is ready
////        if (game.getCurrentPlayerCount() >= MIN_PLAYERS) {
////            startGameWithDelay(game);
////        }
////    }
////
////    // Method to start the game with a delay of 1 minute
////    private void startGameWithDelay(Game game) {
////        CompletableFuture.runAsync(() -> {
////            try {
////                TimeUnit.MINUTES.sleep(1); // Wait for 1 minute
////                if (game.getCurrentPlayerCount() >= MIN_PLAYERS) {
////                    startGame(game);
////                } else {
////                    broadcastEvent("Error", "Not enough players to start the game.");
////                }
////            } catch (InterruptedException e) {
////                Thread.currentThread().interrupt();
////            }
////        });
////    }
////
////    // Method to start the game
////    private void startGame(Game game) {
////        game.setStatus(GameStatus.ACTIVE);
////        game.setStartTime(LocalDateTime.now());
////        gameRepository.save(game);
////
////        // Initialize the deck here to ensure it's ready for dealing cards
////        initializeDeck();
////
////        long totalAnte = postAnte(game.getGameId()); // Get the total ante amount
////        dealCards(game.getGameId());
////
////        // Broadcast that the game has started and show the total ante
////        broadcastEvent("gameStarted", "The game has started! Total Ante: " + totalAnte);
////    }
////
////    // ... (Other methods remain unchanged)
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
////                throw new IllegalStateException("Player " + player.getUser() + " does not have enough chips for the ante.");
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
////    // Method to deal cards
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
