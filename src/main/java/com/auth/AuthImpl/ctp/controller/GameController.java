//package com.auth.AuthImpl.ctp.controller;
//
//import com.auth.AuthImpl.ctp.actionImpl.JoinGameAction;
//import com.auth.AuthImpl.ctp.actionImpl.PlaceBetAction;
//import com.auth.AuthImpl.ctp.actionImpl.ShowCardsAction;
//import com.auth.AuthImpl.ctp.dto.GameEvent;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.enums.PlayerActionType;
//import com.auth.AuthImpl.ctp.service.TeenPattiGameService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.util.List;
//
//@Controller
//public class GameController {
//
//    @Autowired
//    private TeenPattiGameService gameService;
//
//    @Autowired
//    private JoinGameAction joinGameAction;
//    @Autowired
//    private ShowCardsAction showCardsAction;
//
//    @Autowired
//    private PlaceBetAction placeBetAction;
//
//
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @MessageMapping("/gameAction")
//    public void handlePlayerAction(PlayerAction playerAction) {
//        System.out.println("Received action: " + playerAction.getAction() + " for player: " + playerAction.getPlayerId());
//        Long playerId = playerAction.getPlayerId();
//        Long gameId = playerAction.getGameId(); // Retrieve gameId from PlayerAction
//        PlayerActionType action = playerAction.getAction(); // Use the enum
//
//        switch (action) {
//            case JOIN:
//                handleJoin(playerAction);
//                break;
//
//            case BET:
//                boolean isBlind = playerAction.isBlind(); // This method or flag should be present in the `playerAction` class
//                handleBet(playerId, gameId, playerAction.getBetAmount(),isBlind);
//                break;
//
//
////            case DEAL:
////                handleDeal(gameId);
////                break;
//
//            case FOLD:
//                handleFold(playerId, gameId);
//                break;
//
//            case SHOW:
//                handleShow(gameId);
//                break;
//
//            default:
//                handleUnknownAction(action);
//                break;
//        }
//    }
//
//    private void handleJoin(PlayerAction playerAction) {
//        Long playerId = playerAction.getPlayerId();
//        Long gameId = playerAction.getGameId();
//        try {
//            joinGameAction.joinGame(playerId, gameId);
//            broadcastEvent("playerJoined", playerId + " has joined the game");
//            System.out.println("Player " + playerId + " has joined the game with Game ID: " + gameId);
//        } catch (IllegalArgumentException e) {
//            handleGameError(e.getMessage());
//        }
//    }
//
//    private void handleBet(Long playerId, Long gameId, int betAmount, boolean isBlind) {
//        try {
//            // Call the placeBet method with the correct parameters
//            placeBetAction.placeBet(playerId, betAmount, gameId, isBlind);
//
//            // Broadcasting the bet event
//            broadcastEvent("betPlaced", "Player " + playerId + " has placed a bet of " + betAmount + " (Blind: " + isBlind + ")");
//
//            // Logging the bet details to the console
//            System.out.println("Player " + playerId + " placed a bet of " + betAmount + " (Blind: " + isBlind + ")");
//        } catch (IllegalArgumentException e) {
//            // Handle invalid arguments like insufficient chips or invalid bet amount
//            handleGameError("Invalid bet: " + e.getMessage());
//        } catch (IllegalStateException e) {
//            // Handle invalid game states, such as placing a bet when it's not the player's turn
//            handleGameError("Game state error: " + e.getMessage());
//        } catch (Exception e) {
//            // Handle any unexpected errors that might occur
//            handleGameError("Unexpected error: " + e.getMessage());
//            e.printStackTrace();  // Print the stack trace for debugging
//        }
//    }
//
//
//
////    private void handleDeal(Long gameId) {
////        gameService.dealCards(gameId);
////        broadcastEvent("cardsDealt", gameService.getPlayersCards(gameId));
////    }
//
//    private void handleFold(Long playerId, Long gameId) {
//        gameService.foldPlayer(playerId, gameId);
//        broadcastEvent("playerFolded", playerId + " has folded");
//    }
//
//    private void handleShow(Long gameId) {
//        List<String> winningPlayers = showCardsAction.showCards(gameId);
//        broadcastEvent("showResults", winningPlayers);
//    }
//
//    private void handleUnknownAction(PlayerActionType action) {
//        broadcastEvent("error", "Unknown action: " + action.getAction());
//    }
//
//    private void handleGameError(String errorMessage) {
//        broadcastEvent("error", errorMessage);
//        System.out.println("Error during game action: " + errorMessage);
//    }
//
//    private void broadcastEvent(String eventType, Object data) {
//        GameEvent event = new GameEvent();
//        event.setEventType(eventType);
//        event.setEventData(data);
//        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
//    }
//}

package com.auth.AuthImpl.ctp.controller;

import com.auth.AuthImpl.ctp.actionImpl.FoldPlayerAction;
import com.auth.AuthImpl.ctp.actionImpl.JoinGameAction;
import com.auth.AuthImpl.ctp.actionImpl.PlaceBetAction;
import com.auth.AuthImpl.ctp.actionImpl.ShowCardsAction;
import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.dto.PlayerAction;
import com.auth.AuthImpl.ctp.enums.PlayerActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class GameController {

    @Autowired
    private ShowCardsAction showCardsAction;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JoinGameAction joinGameAction;

    @Autowired
    private PlaceBetAction placeBetAction;

    @Autowired
    private FoldPlayerAction foldPlayerAction;

    @MessageMapping("/gameAction")
    public void handlePlayerAction(PlayerAction playerAction) {    //todo
        System.out.println("Received action: " + playerAction.getAction() + " for player: " + playerAction.getPlayerId());
        Long playerId = playerAction.getPlayerId();
        Long gameId = playerAction.getGameId(); // Retrieve gameId from PlayerAction
        PlayerActionType action = playerAction.getAction(); // Use the enum

        switch (action) {
            case JOIN:
                handleJoin(playerAction);
                break;

            case BET:
                boolean isBlind = playerAction.isBlind(); // Ensure this method is present in the `playerAction` class
                handleBet( gameId,playerId, playerAction.getBetAmount());
                break;

            case FOLD:
                handleFold(gameId, playerId);
                break;

            case SHOW:
                handleShow(gameId);
                break;

            default:
                handleUnknownAction(action);
                break;
        }
    }

    private void handleJoin(PlayerAction playerAction) {
        Long playerId = playerAction.getPlayerId();
        Long gameId = playerAction.getGameId();
        try {
            joinGameAction.joinGame(playerId, gameId);
//            broadcastEvent("PLAYER_JOINED", playerId + " has joined the game");
            System.out.println("Player " + playerId + " has joined the game with Game ID: " + gameId);
        } catch (IllegalArgumentException e) {
            handleGameError(e.getMessage());
        }
    }

//    private void handleBet(Long playerId, Long gameId, BigDecimal betAmount) {
//        try {
//            // Call the placeBet method with the correct parameters
//            gameActionService.placeBet(playerId, betAmount);
//
//            // Broadcasting the bet event
////            broadcastEvent("BET_PLACED", "Player " + playerId + " has placed a bet of " + betAmount + " (Blind: " + isBlind + ")");
//
//            // Logging the bet details to the console
////            System.out.println("Player " + playerId + " placed a bet of " + betAmount + " (Blind: " + isBlind + ")");
//        } catch (IllegalArgumentException e) {
//            // Handle invalid arguments like insufficient chips or invalid bet amount
//            handleGameError("Invalid bet: " + e.getMessage());
//        } catch (IllegalStateException e) {
//            // Handle invalid game states, such as placing a bet when it's not the player's turn
//            handleGameError("Game state error: " + e.getMessage());
//        } catch (Exception e) {
//            // Handle any unexpected errors that might occur
//            handleGameError("Unexpected error: " + e.getMessage());
//            e.printStackTrace();  // Print the stack trace for debugging
//        }
//    }
private void handleBet(Long playerId, Long gameId, BigDecimal betAmount) {
    // Validate input parameters before proceeding
    if (playerId == null || gameId == null || betAmount == null || betAmount.compareTo(BigDecimal.ZERO) <= 0) {
        handleGameError("Invalid input: Player ID, Game ID, and Bet Amount must be provided and Bet Amount must be positive.");
        return;  // Early exit if parameters are invalid
    }

    try {
        // Call the placeBet method with the correct parameters
        placeBetAction.placeBet(gameId, playerId, betAmount);

        // Broadcasting the bet event
        broadcastEvent("BET_PLACED", "Player " + playerId + " has placed a bet of " + betAmount);

        // Logging the bet details to the console
        System.out.println("Player " + playerId + " placed a bet of " + betAmount);
    } catch (IllegalArgumentException e) {
        // Handle invalid arguments like insufficient chips or invalid bet amount
        handleGameError("Invalid bet: " + e.getMessage());
    } catch (IllegalStateException e) {
        // Handle invalid game states, such as placing a bet when it's not the player's turn
        handleGameError("Game state error: " + e.getMessage());
    } catch (Exception e) {
        handleGameError("Unexpected error: " + e.getMessage());
        e.printStackTrace();  // This will log the error and its stack trace
        throw e;  // Re-throw if you need to propagate it up
    }

}


    private void handleFold(Long gameId, Long playerId) {
        try {
            foldPlayerAction.fold(gameId,playerId);
            broadcastEvent("PLAYER_FOLDED", playerId + " has folded");
        } catch (IllegalArgumentException e) {
            handleGameError("Error folding: " + e.getMessage());
        }
    }

//    private void handleShow(Long gameId) {
//        try {
//            List<String> winningPlayers = gameActionService.showCards(gameId);
//            broadcastEvent("SHOW_RESULTS", winningPlayers);
//        } catch (Exception e) {
//            handleGameError("Error showing cards: " + e.getMessage());
//        }
//    }
private void handleShow(Long gameId) {
    try {
        // Instead of expecting a return value, call the service method to show cards
        showCardsAction.showCards(gameId);

        // Prepare the broadcast message (you can customize it as needed)
        String message = "Cards have been shown for game ID " + gameId + ".";

        // Broadcast the results of the show
        broadcastEvent("SHOW_RESULTS", message);
    } catch (Exception e) {
        handleGameError("Error showing cards for game ID " + gameId + ": " + e.getMessage());
    }
}



    private void handleUnknownAction(PlayerActionType action) {
        broadcastEvent("ERROR", "Unknown action: " + action.getAction());
    }

    private void handleGameError(String errorMessage) {
        broadcastEvent("ERROR", errorMessage);
        System.out.println("Error during game action: " + errorMessage);
    }

    private void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }
}
