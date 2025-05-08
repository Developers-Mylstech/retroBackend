package com.mylstech.rentro.repository;

import com.mylstech.rentro.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageUrl(String imageUrl);

    @Query("SELECT i FROM Image i WHERE i.products IS EMPTY")
    List<Image> findImagesWithNoProducts();
}
