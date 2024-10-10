package com.auth.AuthImpl.registraion.repo;

import com.auth.AuthImpl.registraion.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long>  {

    }
