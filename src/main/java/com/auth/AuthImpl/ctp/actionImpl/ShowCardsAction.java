//package com.auth.AuthImpl.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.service.playeractionservice.PlayerActionInterface;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class ShowCardsAction implements PlayerActionInterface {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    private PlayerAction playerAction;
//
//    public ShowCardsAction(PlayerAction playerAction) {
//        this.playerAction = playerAction;
//    }
//
//    @Override
//    public void execute() {
//        Long gameId = playerAction.getGameId();
//        List<PlayerGame> playersInGame = playerGameRepository.findByGameId(gameId);
//        List<String> visibleCards = new ArrayList<>();
//
//        for (PlayerGame playerGame : playersInGame) {
//            if (!playerGame.getHasFolded()) {
//                visibleCards.add("Player " + playerGame.getUserId() + ": " + playerGame.getCards());
//            }
//        }
//
//        System.out.println("Visible Cards: " + visibleCards);
//    }
//}
