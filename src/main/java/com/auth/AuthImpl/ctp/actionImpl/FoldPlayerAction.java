package com.auth.AuthImpl.ctp.actionImpl;

import com.auth.AuthImpl.ctp.entity.PlayerGame;
import com.auth.AuthImpl.ctp.enums.GameResult;
import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
import com.auth.AuthImpl.ctp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoldPlayerAction{

    @Autowired
    private PlayerGameRepository playerGameRepository;
    public void foldPlayer(Long playerId, Long gameId) {
        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));

        playerGame.setHasFolded(true);
        playerGame.setResult(GameResult.LOST);
        playerGameRepository.save(playerGame);
    }
}
