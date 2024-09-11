package com.application.Application.registraion.repo;

import com.application.Application.registraion.entity.Role;
import com.application.Application.registraion.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(Roles roleName);
}

