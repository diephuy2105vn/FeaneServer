package com.springboot.server.repository;

import com.springboot.server.models.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:q% OR u.name LIKE %:q%")
    Set<User> findByUsernameOrNameContaining(String q);
}
