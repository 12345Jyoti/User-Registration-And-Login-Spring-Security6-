//package com.auth.AuthImpl.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.service.playeractionservice.PlayerActionInterface;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FoldPlayerAction implements PlayerActionInterface {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    private PlayerAction playerAction;
//
//    public FoldPlayerAction(PlayerAction playerAction) {
//        this.playerAction = playerAction;
//    }
//
//    @Override
//    public void execute() {
//        Long playerId = playerAction.getPlayerId();
//        Long gameId = playerAction.getGameId();
//
//        PlayerGame playerGame = playerGameRepository.findByUserIdAndGameId(playerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Player is not part of the game"));
//
//        playerGame.setHasFolded(true);
//        playerGameRepository.save(playerGame);
//    }
//}
