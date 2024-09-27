package com.auth.AuthImpl.ctp.repository;

import com.auth.AuthImpl.ctp.entity.Game;
import com.auth.AuthImpl.ctp.enums.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllByStatus(GameStatus gameStatus);

    List<Game> findByStatus(GameStatus gameStatus);
    // Additional query methods can be defined here
}

