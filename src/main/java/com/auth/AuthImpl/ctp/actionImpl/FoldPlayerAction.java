package com.auth.AuthImpl.ctp.actionImpl;

import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.enums.GameCurrentStatus;
import com.auth.AuthImpl.ctp.enums.PlayerGameResult;
import com.auth.AuthImpl.ctp.nenity.GameInstance;
import com.auth.AuthImpl.ctp.nenity.GamePlayer;
import com.auth.AuthImpl.ctp.nenity.GameResult;
import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
import com.auth.AuthImpl.ctp.repository.GamePlayerRepository;
import com.auth.AuthImpl.ctp.repository.GameResultRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import com.auth.AuthImpl.registraion.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Service
public class FoldPlayerAction{
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameInstanceRepository gameInstanceRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private TurnManager turnManager;

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private List<String> deck = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private GameInstance currentGameInstance;
    private static final int ANTE_AMOUNT = 100;
    private BigDecimal currentPot;
    private BigDecimal currentBet;

    public void fold(Long gameId, Long playerId) {

        if (gameId == null || playerId == null) {
            throw new IllegalArgumentException("Game ID and Player ID must not be null.");
        }

        // Fetch the player who is folding
        GamePlayer gamePlayer = gamePlayerRepository.findByGameIdAndPlayerId(gameId, playerId)
                .orElseThrow(() -> new RuntimeException("Player not found in the game."));

        // Ensure the player is still active (not folded already)
        if (gamePlayer.getGameCurrentStatus() == GameCurrentStatus.fold) {
            throw new IllegalStateException("Player has already folded and cannot fold again.");
        }
        // Update the player's status to reflect they have folded
        gamePlayer.setGameCurrentStatus(GameCurrentStatus.fold);
        gamePlayer.setResult(PlayerGameResult.lose);
        gamePlayer.setStatus(Status.HISTORY);
        gamePlayer.setCreatedBy("SYSTEM");
        gamePlayerRepository.save(gamePlayer);

        // Broadcast the fold event
        broadcastEvent("PLAYER_FOLDED", "Player " + playerId + " has folded.");

        // Check if the game should end (e.g., if all players but one have folded)
        checkIfGameOverAfterFold(gamePlayer.getGameId());
    }

    private void checkIfGameOverAfterFold(Long gameId) {
        // Fetch all players participating in the game
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);

        // Filter active players
        List<GamePlayer> activePlayers = playersInGame.stream()
                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold)
                .collect(Collectors.toList());

        // If there is only one active player left, declare them the winner
        if (activePlayers.size() == 1) {
            GamePlayer winner = activePlayers.get(0);
            endGameAndSaveResult(gameId, winner);
        }
    }

    private void endGameAndSaveResult(Long gameId, GamePlayer winner) {
        // Declare the game is over and save the result in GameResult table
        GameResult gameResult = new GameResult();
        gameResult.setGameId(gameId);
        gameResult.setWinningPlayerId(winner.getPlayerId().intValue());
        gameResult.setWinningAmount(calculateWinningAmount(gameId));
        gameResult.setCreatedAt(LocalDateTime.now());
        gameResult.setCreatedBy("ADMIN");

        // Save the game result
        gameResultRepository.save(gameResult);

        // Update the winner's result to WON and all others to LOST
        updatePlayerResults(gameId, winner);

        // Broadcast that the game has ended
        broadcastEvent("GAME_OVER", "Game " + gameId + " has ended. Player " + winner.getPlayerId() + " is the winner.");
    }

    private void updatePlayerResults(Long gameId, GamePlayer winner) {
        // Update the winner's result
        winner.setResult(PlayerGameResult.win);
        gamePlayerRepository.save(winner);

        // Mark all other players as LOST
        gamePlayerRepository.findByGameId(gameId).stream()
                .filter(player -> !player.getPlayerId().equals(winner.getPlayerId()))
                .forEach(player -> {
                    player.setResult(PlayerGameResult.lose);
                    gamePlayerRepository.save(player);
                });
    }

    //todo: calculate correct winning amount
    private BigDecimal calculateWinningAmount(Long gameId) {
        // Fetch all players participating in the game
        List<GamePlayer> playersInGame = gamePlayerRepository.findByGameId(gameId);

        // Calculate total bets from active players (not folded)
        BigDecimal totalBets = playersInGame.stream()
                .filter(player -> player.getGameCurrentStatus() != GameCurrentStatus.fold) // Consider only active players
                .map(GamePlayer::getPlayerBettingAmount) // Assuming there's a method to get the player's bet amount
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up all bets

        // Implement additional logic if needed for determining the winning amount
        // For example, you might have a house cut, game rules, etc.
        // Here is a simple logic example (adjust based on your game rules):
        BigDecimal houseCutPercentage = new BigDecimal("0.05"); // Assume 5% house cut
        BigDecimal houseCut = totalBets.multiply(houseCutPercentage);

        // Calculate the final winning amount for the winner (total bets - house cut)
        BigDecimal winningAmount = totalBets.subtract(houseCut);

        return winningAmount;
    }


    protected void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }


}

//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//    public void foldPlayer(Long playerId, Long gameId) {
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//        playerGame.setHasFolded(true);
//        playerGame.setResult(GameResult.LOST);
//        playerGameRepository.save(playerGame);
//    }
//}
