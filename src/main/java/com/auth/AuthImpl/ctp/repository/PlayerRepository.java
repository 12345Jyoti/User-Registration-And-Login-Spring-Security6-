package com.auth.AuthImpl.ctp.repository;


import com.auth.AuthImpl.ctp.nenity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Example of a custom query method
    Player findByUserId(Long userId);

    // You can add more query methods as needed
}

//