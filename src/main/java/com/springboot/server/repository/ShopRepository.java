package com.springboot.server.repository;

import com.springboot.server.models.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("SELECT s from Shop s JOIN s.owner o WHERE  s.name = :name and o.username = :username")
    Optional<Shop> findByNameAndUsername(@Param("name") String name, @Param("username") String username);

    @Query("SELECT s from Shop s JOIN s.owner o WHERE o.username = :username")
    Set<Shop> findAllByUsername(@Param("username") String username);
}
