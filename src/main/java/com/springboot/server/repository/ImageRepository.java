package com.springboot.server.repository;

import com.springboot.server.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByPublicId(String publicId);
    void deleteByPublicId(String publicId);
}
