package com.auth.AuthImpl.ctp.repository;

import com.auth.AuthImpl.ctp.enums.GameCurrentStatus;
import com.auth.AuthImpl.ctp.enums.GameStatus;
import com.auth.AuthImpl.ctp.nenity.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GamePlayerRepository extends JpaRepository<GamePlayer,Long> {
    List<GamePlayer> findByGameId(Long Id);

    Optional<GamePlayer> findByGameIdAndPlayerId(Long gameId, Long playerId);

    List<GamePlayer> findByGameIdAndPlayerBettingAmountGreaterThan(Long gameId, BigDecimal zero);

    List<GamePlayer> findByGameIdAndGameCurrentStatus(Long gameId, GameCurrentStatus gameCurrentStatus);

    Optional <GamePlayer> findByPlayerId(Long playerId);

    List<GamePlayer> findByGameIdAndGameStatus(Long gameId, GameStatus gameStatus);

    List<GamePlayer> findByGameIdAndGameCurrentStatusNot(Long gameId, GameCurrentStatus status);
}
