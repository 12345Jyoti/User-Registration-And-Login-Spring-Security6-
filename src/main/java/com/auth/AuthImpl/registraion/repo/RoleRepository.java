package com.auth.AuthImpl.registraion.repo;

import com.auth.AuthImpl.registraion.entity.Role;
import com.auth.AuthImpl.registraion.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(Roles roleName);
}

