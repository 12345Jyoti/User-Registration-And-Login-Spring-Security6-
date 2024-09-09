package com.application.Application.repo;

import com.application.Application.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository <Users,Integer>{
    Users findByUserName(String username);
}
