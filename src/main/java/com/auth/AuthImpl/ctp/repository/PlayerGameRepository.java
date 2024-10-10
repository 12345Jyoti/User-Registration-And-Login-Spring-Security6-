//package com.auth.AuthImpl.ctp.repository;
//
//import com.auth.AuthImpl.ctp.entity.PlayerGame;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface PlayerGameRepository extends JpaRepository<PlayerGame, Long> {
//    List<PlayerGame> findByGameId(Long gameId);
//    Optional<PlayerGame> findByUserIdAndGameId(Long userId, Long gameId);
//
//    boolean existsByUserIdAndGameId(Long userId, Long gameId);
//}
