//package com.auth.AuthImpl.ctp.service;
//
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.enums.GameStatus;
//import com.auth.AuthImpl.ctp.repository.GameRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class GameService {
//
//    @Autowired
//    private GameRepository gameRepository;
//
//    public Game createGame() {
//        Game newGame = new Game();
//        newGame.setStatus(GameStatus.WAITING_FOR_PLAYERS);  // Example status, can be modified
//        newGame.setStartTime(LocalDateTime.now());
//        newGame.setTotalPot(0L);  // Initialize total pot to 0
//
//        return gameRepository.save(newGame);
//    }
//}
