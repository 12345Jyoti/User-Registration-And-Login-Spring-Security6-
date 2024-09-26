package com.auth.AuthImpl.ctp.controller;

import com.auth.AuthImpl.ctp.dto.GameEvent;
import com.auth.AuthImpl.ctp.dto.PlayerAction;
import com.auth.AuthImpl.ctp.enums.PlayerActionType;
import com.auth.AuthImpl.ctp.service.TeenPattiGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController {

    @Autowired
    private TeenPattiGameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/gameAction")
    public void handlePlayerAction(PlayerAction playerAction) {
        System.out.println("Received action: " + playerAction.getAction() + " for player: " + playerAction.getPlayerId());
        Long playerId = playerAction.getPlayerId();
        Long gameId = playerAction.getGameId(); // Retrieve gameId from PlayerAction
        PlayerActionType action = playerAction.getAction(); // Use the enum

        switch (action) {
            case JOIN:
                handleJoin(playerAction);
                break;

            case BET:
//                boolean isBlind = playerAction.isBlind(); // This method or flag should be present in the `playerAction` class
                handleBet(playerId, gameId, playerAction.getBetAmount());
                break;


//            case DEAL:
//                handleDeal(gameId);
//                break;

            case FOLD:
                handleFold(playerId, gameId);
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
            gameService.joinGame(playerId, gameId);
            broadcastEvent("playerJoined", playerId + " has joined the game");
            System.out.println("Player " + playerId + " has joined the game with Game ID: " + gameId);
        } catch (IllegalArgumentException e) {
            handleGameError(e.getMessage());
        }
    }

    private void handleBet(Long playerId, Long gameId, int betAmount) {
        try {
            // Updated to match the `placeBet` method signature
            gameService.placeBet(playerId, betAmount, gameId);
            // Broadcasting and logging the bet event
            broadcastEvent("betPlaced", "Player " + playerId + " has placed a bet of " + betAmount);
//            System.out.println("Player " + playerId + " placed a bet of " + betAmount + " (Blind: " + isBlind + ")");
        } catch (IllegalArgumentException e) {
            handleGameError(e.getMessage());
        } catch (IllegalStateException e) {
            handleGameError(e.getMessage());
        }
    }


//    private void handleDeal(Long gameId) {
//        gameService.dealCards(gameId);
//        broadcastEvent("cardsDealt", gameService.getPlayersCards(gameId));
//    }

    private void handleFold(Long playerId, Long gameId) {
        gameService.foldPlayer(playerId, gameId);
        broadcastEvent("playerFolded", playerId + " has folded");
    }

    private void handleShow(Long gameId) {
        List<String> winningPlayers = gameService.showCards(gameId);
        broadcastEvent("showResults", winningPlayers);
    }

    private void handleUnknownAction(PlayerActionType action) {
        broadcastEvent("error", "Unknown action: " + action.getAction());
    }

    private void handleGameError(String errorMessage) {
        broadcastEvent("error", errorMessage);
        System.out.println("Error during game action: " + errorMessage);
    }

    private void broadcastEvent(String eventType, Object data) {
        GameEvent event = new GameEvent();
        event.setEventType(eventType);
        event.setEventData(data);
        messagingTemplate.convertAndSend("/topic/gameUpdates", event);
    }
}
