package com.springboot.server.repository;

import com.springboot.server.models.ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopCartRepository extends JpaRepository<ShopCart, Long> {
    @Query("SELECT sc from ShopCart sc\n" +
            "JOIN sc.cart c\n" +
            "JOIN sc.shop s\n" +
            "WHERE s.id = :shopId and c.id = :cartId")
    ShopCart findByShopIdAndCartId(@Param("shopId") Long shopId, @Param("cartId") Long cartId);
}
