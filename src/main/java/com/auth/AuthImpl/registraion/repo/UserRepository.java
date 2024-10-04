package com.auth.AuthImpl.registraion.repo;


import com.auth.AuthImpl.registraion.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Optional<Users> findByEmail(String email);

    Optional<Users> findByPhoneNumber(String phoneNumber);

    Users findByPhoneNumberAndIsdCode(String phoneNumber, String isdCode);

//    Users findByEmail(String email);

//    Optional<Users> findByIsdCodeInAndPhoneNumber(String isdCode, String phoneNumber);
Optional<Users> findByIsdCodeInAndPhoneNumber(Collection<String> isdCode, String phoneNumber);
}

