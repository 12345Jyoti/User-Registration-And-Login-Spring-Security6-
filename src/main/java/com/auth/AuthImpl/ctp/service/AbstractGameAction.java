package com.auth.AuthImpl.ctp.service;

import com.auth.AuthImpl.ctp.nenity.GamePlayer;

import java.math.BigDecimal;

public abstract class AbstractGameAction {

    /**
     * Method for player joining a game.
     * @param playerId The ID of the player joining.
     * @param gameId The ID of the game instance.
     */
    public abstract void joinGame(Long playerId, Long gameId);

    /**
     * Method for placing a bet by the player.
     * @param playerId The ID of the player placing the bet.
     * @param betAmount The amount of the bet.
     */
    public abstract void placeBet(Long playerId, BigDecimal betAmount);

    /**
     * Method for initiating a side show action by the player.
     * @param playerId The ID of the player initiating the side show.
     * @param opponentId The ID of the opponent player.
     */
    public abstract void sideShow(Long playerId, Long opponentId);

    /**
     * Method for showing the cards of a player.
     * @param playerId The ID of the player showing their cards.
     */
    public abstract void showCards(Long playerId);

    /**
     * Method for folding (quitting the game) by a player.
     * @param playerId The ID of the player folding.
     */
    public abstract void fold(Long playerId);

    /**
     * Method for broadcasting a game event to players.
     * @param eventType The type of event (e.g., "PLAYER_JOINED", "BET_PLACED").
     * @param data The associated data for the event.
     */
    protected abstract void broadcastEvent(String eventType, Object data);
}

