package com.springboot.server.repository;

import com.springboot.server.models.OrderTb;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderTb, Long> {
    @Query("SELECT o from OrderTb o JOIN o.user u WHERE u.username = :username")
    List<OrderTb> findAllByUsername(@Param("username") String username);

    @Query("SELECT o from OrderTb o JOIN o.shop s WHERE s.id = :shopId")
    List<OrderTb> findAllByShopId(@Param("shopId") long shopId);

    @Query("SELECT o from OrderTb o JOIN o.user u WHERE u.id = :userId")
    List<OrderTb> findAllByUserId(@Param("userId") long userId);
    @Query("SELECT o from OrderTb o JOIN o.shop s WHERE s.id = :shopId")
    List<OrderTb> findAllByShopId(@Param("shopId") long shopId, Pageable pageable);


    @Query("SELECT o from OrderTb o JOIN o.shop s WHERE s.id = :shopId and o.status = :status")
    List<OrderTb> findAllByShopIdAndStatus(@Param("shopId") long shopId,@Param("status") String status);
    @Query("SELECT o from OrderTb o JOIN o.shop s WHERE s.id = :shopId and o.status = :status")
    List<OrderTb> findAllByShopIdAndStatus(@Param("shopId") long shopId,@Param("status") String status, Pageable pageable);
    @Query("SELECT o from OrderTb o JOIN o.shop s WHERE s.id = :shopId and o.id = :orderId")
    Optional<OrderTb> findByShopIdAndOrderId(@Param("shopId") long shopId, @Param("orderId") long orderId);


}
