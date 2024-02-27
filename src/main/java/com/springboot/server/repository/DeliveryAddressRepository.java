package com.springboot.server.repository;

import com.springboot.server.models.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Set<DeliveryAddress> findByUserId(Long userId);
    Optional<DeliveryAddress> findByIdAndIsDefaultFalse(Long id);
    Optional<DeliveryAddress> findByUserIdAndIsDefaultTrue(Long userId);
}