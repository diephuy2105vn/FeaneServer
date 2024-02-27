package com.springboot.server.repository;

import java.util.Optional;

import com.springboot.server.payload.constants.ERole;
import com.springboot.server.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}