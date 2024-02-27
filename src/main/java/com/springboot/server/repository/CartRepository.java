package com.springboot.server.repository;

import com.springboot.server.models.Cart;
import com.springboot.server.models.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c from Cart c JOIN c.user u WHERE u.username = :username")
    Optional<Cart> findByUsername(@Param("username") String username);
}
