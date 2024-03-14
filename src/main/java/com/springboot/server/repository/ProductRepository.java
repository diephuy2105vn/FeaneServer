package com.springboot.server.repository;

import com.springboot.server.models.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT COUNT(p) from Product p WHERE p.name LIKE %:q%")
    int countByNameContaining(String q);
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:q%")
    List<Product> findAllByNameContaining(String q,  Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p JOIN p.shop s WHERE s.id = :shopId and  p.name LIKE %:q%")
    int countByShopIdAndNameContaining(Long shopId, String q);

    @Query("SELECT p FROM Product p JOIN p.shop s WHERE s.id = :shopId and  p.name LIKE %:q%")
    List<Product> findAllByShopIdAndNameContaining(Long shopId, String q,  Pageable pageable);

    @Query("SELECT COUNT(p) from Product p WHERE p.type = :type")
    int countByType(@Param("type") String type);
    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId and p.type = :type")
    List<Product> findAllByType(String type, Pageable pageable);

    @Query("SELECT COUNT(p) from Product p JOIN p.shop s WHERE s.id = :shopId")
    int countByShopId(long shopId);
    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId")
    List<Product> findAllByShopId(long shopId, Pageable pageable);

    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId and p.id = :productId")
    Optional<Product> findByShopIdAndProductId(long shopId, long productId);

    @Query("SELECT COUNT(p) from Product p JOIN p.shop s WHERE p.type = :type and s.id = :shopId")
    int countByShopIdAndType(@Param("shopId") Long shopId, @Param("type") String type);
    @Query("SELECT p from Product p JOIN p.shop s WHERE s.id = :shopId and p.type = :type")
    List<Product> findAllByShopIdAndType( long shopId,String type, Pageable pageable);
}
