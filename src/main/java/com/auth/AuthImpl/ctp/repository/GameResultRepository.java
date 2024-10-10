package com.auth.AuthImpl.ctp.repository;


import com.auth.AuthImpl.ctp.nenity.GameResult; // Adjust the import based on your package structure
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    // You can add custom query methods here if needed
}

