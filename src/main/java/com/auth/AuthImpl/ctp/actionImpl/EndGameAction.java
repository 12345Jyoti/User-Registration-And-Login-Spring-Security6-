//package com.auth.AuthImpl.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import com.auth.AuthImpl.ctp.service.playeractionservice.PlayerActionInterface;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EndGameAction implements PlayerActionInterface {
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    private PlayerAction playerAction;
//
//    public EndGameAction(PlayerAction playerAction) {
//        this.playerAction = playerAction;
//    }
//
//    @Override
//    public void execute() {
//        Long gameId = playerAction.getGameId();
//
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
//
//        game.setStatus(GameStatus.WAITING_FOR_PLAYERS);
//        gameRepository.save(game);
//    }
//}
