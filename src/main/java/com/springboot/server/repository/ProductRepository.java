package com.springboot.server.repository;

import com.springboot.server.models.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId and p.type = :type")
    List<Product> findAllByType(String type, Pageable pageable);
    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId")
    List<Product> findAllByShopId(long shopId, Pageable pageable);
    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId and p.type = :type")
    List<Product> findAllByTypeAndShopId(String type, long shopId, Pageable pageable);

}
