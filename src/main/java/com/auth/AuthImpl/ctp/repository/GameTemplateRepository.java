package com.auth.AuthImpl.ctp.repository;

import com.auth.AuthImpl.ctp.nenity.GameInstance;
import com.auth.AuthImpl.ctp.nenity.GameTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameTemplateRepository extends JpaRepository<GameTemplate, Long> {
}