//package com.auth.AuthImpl.ctp.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.enums.GameResult;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.service.playeractionservice.PlayerActionInterface;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ResetGameAction implements PlayerActionInterface {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    private PlayerAction playerAction;
//
//    public ResetGameAction(PlayerAction playerAction) {
//        this.playerAction = playerAction;
//    }
//
//    @Override
//    public void execute() {
//        Long gameId = playerAction.getGameId();
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//
//        for (PlayerGame playerGame : playersInGame) {
//            playerGame.setCards(null);
//            playerGame.setBetAmount(0L);
//            playerGame.setHasFolded(false);
//            playerGame.setResult(GameResult.PENDING);
//            playerGameRepository.save(playerGame);
//        }
//
//        // Reset game state
//        // Assume there are methods to reset game state and deck in the service layer
//    }
//}
