package com.application.Application.repo;

import com.application.Application.entity.Role;
import com.application.Application.common.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Custom method to find a role by its enum name
    Role findByRoleName(Roles roleName);
}

