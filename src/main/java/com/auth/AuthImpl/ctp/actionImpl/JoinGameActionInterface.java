//package com.auth.AuthImpl.ctp.actionImpl;
//
//
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.enums.GameResult;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.registraion.entity.Users;
//import com.auth.AuthImpl.registraion.repo.UserRepository;
//import com.auth.AuthImpl.ctp.service.TeenPattiGameService; // Assuming this service deals cards
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class JoinGameAction {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private GameRepository gameRepository;
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//    @Autowired
//    private TeenPattiGameService teenPattiGameService; // Inject the game service to deal cards
//
//    private PlayerAction playerAction;
//
//    public JoinGameAction(PlayerAction playerAction) {
//        this.playerAction = playerAction;
//    }
//
//    public void execute() {
//        Long userId = playerAction.getPlayerId();
//        Long gameId = playerAction.getGameId();
//
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
//
//        if (playerGameRepository.existsByUserIdAndGameId(userId, gameId)) {
//            throw new IllegalStateException("User has already joined this game.");
//        }
//
//        if (game.getCurrentPlayerCount() >= game.getMaxPlayers()) {
//            throw new IllegalStateException("Game is full. Cannot join.");
//        }
//
//        // Update current player count
//        game.setCurrentPlayerCount(game.getCurrentPlayerCount() + 1);
//        gameRepository.save(game);
//
//        // Create PlayerGame entry
//        PlayerGame playerGame = new PlayerGame();
//        playerGame.setUserId(userId);
//        playerGame.setGameId(game.getGameId());
//        playerGame.setUser(user);
//        playerGame.setGame(game);
//        playerGame.setBetAmount(0L); // Initialize bet amount to 0
//        playerGame.setResult(GameResult.PENDING); // Set initial result state
//        playerGameRepository.save(playerGame); // Save player game record
//
//        // Check if the minimum player count is reached to start the game
//        if (game.getCurrentPlayerCount() >= game.getMinPlayers()) {
//            startGame(game); // Start the game if enough players are present
//        }
//    }
//
//    private void startGame(Game game) {
//        game.setStatus(GameStatus.ACTIVE);
//        gameRepository.save(game); // Save updated game state
//
//        // Call the method to deal cards to players
//        teenPattiGameService.dealCards(game);
//    }
//}
//
