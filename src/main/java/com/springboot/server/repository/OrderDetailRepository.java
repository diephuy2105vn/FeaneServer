package com.springboot.server.repository;

import com.springboot.server.models.CartDetail;
import com.springboot.server.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT od from OrderDetail od\n" +
            "JOIN od.orderTb o\n" +
            "JOIN od.product p\n" +
            "WHERE o.id = :orderId and p.id = :productId")
    OrderDetail findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
}
