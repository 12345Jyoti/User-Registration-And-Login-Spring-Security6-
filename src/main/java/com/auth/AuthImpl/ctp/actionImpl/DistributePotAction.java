//package com.auth.AuthImpl.actionImpl;
//
//import com.auth.AuthImpl.ctp.entity.Player;
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import com.auth.AuthImpl.ctp.dto.PlayerAction;
//import com.auth.AuthImpl.ctp.repository.PlayerGameRepository;
//import com.auth.AuthImpl.ctp.repository.PlayerRepository;
//import com.auth.AuthImpl.ctp.service.playeractionservice.PlayerActionInterface;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class DistributePotAction implements PlayerActionInterface {
//
//    @Autowired
//    private PlayerGameRepository playerGameRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    private PlayerAction playerAction;
//
//    public DistributePotAction(PlayerAction playerAction) {
//        this.playerAction = playerAction;
//    }
//
//    @Override
//    public void execute() {
//        Long winnerId = playerAction.getPlayerId();
//        Long gameId = playerAction.getGameId();
//
//        PlayerGame winnerGame = playerGameRepository.findByUserIdAndGameId(winnerId, gameId)
//                .orElseThrow(() -> new IllegalArgumentException("Winner not found"));
//
//        Player winner = playerRepository.findById(winnerId)
//                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
//
//        // Assuming currentPot is managed elsewhere, for example, in a GameService
//        int currentPot = 100; // Placeholder for the current pot value
//        winner.setChips(winner.getChips() + currentPot);
//
//        playerRepository.save(winner);
//    }
//}
