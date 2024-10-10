//package com.auth.AuthImpl.ctp.repository;
//
//import com.auth.AuthImpl.ctp.entity.Game;
//import com.auth.AuthImpl.ctp.enums.GameTempStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface GameRepository extends JpaRepository<Game, Long> {
//    List<Game> findAllByStatus(GameTempStatus gameTempStatus);
//
//    List<Game> findByStatus(GameTempStatus gameTempStatus);
//    // Additional query methods can be defined here
//}
//
