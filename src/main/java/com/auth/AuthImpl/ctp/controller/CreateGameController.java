//
//package com.auth.AuthImpl.ctp.controller;
//
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.service.GameService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/game")
//public class CreateGameController {
//
//    @Autowired
//    private GameService gameService;
//
//    @PostMapping("/create")
//    public ResponseEntity<Game> createGame() {
//        Game newGame = gameService.createGame();
//        return ResponseEntity.ok(newGame);
//    }
//}
