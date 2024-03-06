package com.springboot.server.repository;

import com.springboot.server.models.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

    @Query("SELECT cd from CartDetail cd\n" +
            "JOIN cd.shopCart sc\n" +
            "JOIN cd.product p\n" +
            "WHERE sc.id = :shopCartId and p.id = :productId")
    CartDetail findByShopCartIdAndProductId(@Param("shopCartId") Long shopCartId,  @Param("productId") Long productId);

    @Query("SELECT cd FROM CartDetail cd \n" +
            "JOIN cd.shopCart sc \n" +
            "JOIN sc.cart c \n" +
            "JOIN cd.product p \n" +
            "WHERE c.id = :cartId AND p.id = :productId")
    CartDetail findByCartIdAndProductId(@Param("cartId") Long cartId,  @Param("productId") Long productId);
}

