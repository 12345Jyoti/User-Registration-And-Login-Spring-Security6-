package com.auth.AuthImpl.ctp.repository;

import com.auth.AuthImpl.ctp.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    // Additional query methods can be defined here
}

