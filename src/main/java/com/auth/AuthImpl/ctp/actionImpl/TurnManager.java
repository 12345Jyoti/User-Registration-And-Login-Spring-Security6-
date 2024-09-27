package com.auth.AuthImpl.ctp.actionImpl;
////
////import com.auth.AuthImpl.ctp.entity.PlayerGame;
////import org.springframework.stereotype.Component;
////
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
////@Component
////public class TurnManager {
////    private final Map<Long, Long> currentPlayers = new HashMap<>(); // gameId -> currentPlayerId
////
////    public Long getCurrentPlayerId(Long gameId) {
////        return currentPlayers.get(gameId);
////    }
////
////    public void setCurrentPlayerId(Long gameId, Long playerId) {
////        if (playerId == null) {
////            throw new IllegalArgumentException("Player ID cannot be null");
////        }
////        System.out.println("Setting current player ID: " + playerId + " for game ID: " + gameId);
////        currentPlayers.put(gameId, playerId);
////    }
////
////    public void moveToNextPlayer(Long gameId, List<PlayerGame> playersInGame) {
////        Long currentPlayerId = getCurrentPlayerId(gameId);
////        if (currentPlayerId == null) {
////            throw new IllegalStateException("Current player is not set for game ID: " + gameId);
////        }
////
////        // Find the current player
////        PlayerGame currentPlayerGame = playersInGame.stream()
////                .filter(p -> p.getUserId().equals(currentPlayerId))
////                .findFirst()
////                .orElseThrow(() -> new IllegalStateException("Current player not found"));
////
////        // Move to the next player
////        int currentIndex = playersInGame.indexOf(currentPlayerGame);
////        int nextIndex = (currentIndex + 1) % playersInGame.size();
////        PlayerGame nextPlayerGame = playersInGame.get(nextIndex);
////
////        // Update the current player
////        setCurrentPlayerId(gameId, nextPlayerGame.getUserId());
////    }
////}
//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class TurnManager {
//    private final Map<Long, Long> currentPlayers = new HashMap<>(); // gameId -> currentPlayerId
//
//    public Long getCurrentPlayerId(Long gameId) {
//        return currentPlayers.get(gameId);
//    }
//
//    public void setCurrentPlayerId(Long gameId, Long playerId) {
//        if (playerId == null) {
//            throw new IllegalArgumentException("Player ID cannot be null");
//        }
//        System.out.println("Setting current player ID: " + playerId + " for game ID: " + gameId);
//        currentPlayers.put(gameId, playerId);
//    }
//
//    public void moveToNextPlayer(Long gameId, List<PlayerGame> playersInGame) {
//        Long currentPlayerId = getCurrentPlayerId(gameId);
//        if (currentPlayerId == null) {
//            throw new IllegalStateException("Current player is not set for game ID: " + gameId);
//        }
//
//        // Find the current player
//        PlayerGame currentPlayerGame = playersInGame.stream()
//                .filter(p -> p.getUserId().equals(currentPlayerId))
//                .findFirst()
//                .orElseThrow(() -> new IllegalStateException("Current player not found"));
//
//        // Move to the next player
//        int currentIndex = playersInGame.indexOf(currentPlayerGame);
//        int nextIndex = (currentIndex + 1) % playersInGame.size();
//        PlayerGame nextPlayerGame = playersInGame.get(nextIndex);
//
//        // Update the current player
//        setCurrentPlayerId(gameId, nextPlayerGame.getUserId());
//    }}

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TurnManager {

    private final Map<Long, Long> currentPlayerMap = new HashMap<>(); // Map of gameId to currentPlayerId
    private final Map<Long, List<Long>> playerOrderMap = new HashMap<>(); // Map of gameId to player order

    // Set the current player for a game
    public void setCurrentPlayerId(Long gameId, Long playerId) {
        currentPlayerMap.put(gameId, playerId);
    }

    // Get the current player for a game
    public Long getCurrentPlayerId(Long gameId) {
        return currentPlayerMap.get(gameId);
    }

    // Set the player order for a game
    public void setPlayerOrder(Long gameId, List<Long> playerIds) {
        playerOrderMap.put(gameId, playerIds);
    }

    // Get the player order for a game
    public List<Long> getPlayerOrder(Long gameId) {
        return playerOrderMap.get(gameId);
    }

    // Move to the next player
    public void moveToNextPlayer(Long gameId) {
        List<Long> playerIds = playerOrderMap.get(gameId);
        if (playerIds == null || playerIds.isEmpty()) {
            throw new IllegalStateException("No player order found for the game.");
        }

        Long currentPlayerId = getCurrentPlayerId(gameId);
        if (currentPlayerId == null) {
            throw new IllegalStateException("No current player found for the game.");
        }

        int currentIndex = playerIds.indexOf(currentPlayerId);
        int nextIndex = (currentIndex + 1) % playerIds.size(); // Wrap around to the first player
        setCurrentPlayerId(gameId, playerIds.get(nextIndex));
    }
}
